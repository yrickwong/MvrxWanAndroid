package com.yrickwong.tech.mvrx.feature.wechat

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.*
import com.yrickwong.tech.mvrx.R
import com.yrickwong.tech.mvrx.bean.Article
import com.yrickwong.tech.mvrx.bean.HttpResult
import com.yrickwong.tech.mvrx.core.BaseFragment
import com.yrickwong.tech.mvrx.core.MvRxEpoxyController
import com.yrickwong.tech.mvrx.core.MvRxViewModel
import com.yrickwong.tech.mvrx.core.simpleController
import com.yrickwong.tech.mvrx.feature.banner.BannerState
import com.yrickwong.tech.mvrx.feature.webview.WebViewDetailArgs
import com.yrickwong.tech.mvrx.network.ApiService
import com.yrickwong.tech.mvrx.tab.TAG
import com.yrickwong.tech.mvrx.views.articleRow
import com.yrickwong.tech.mvrx.views.loadingRow
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.android.ext.android.inject


@SuppressLint("ParcelCreator")
@Parcelize
data class KnowledgeArgs(val id: Int) : Parcelable

data class KnowledgeState(
    val articles: List<Article> = emptyList(),
    val id: Int,
    val page: Int = 0,
    val request: Async<HttpResult<List<Article>>> = Uninitialized
) : MvRxState {
    /**
     * This secondary constructor will automatically called if your Fragment has
     * a parcelable in its arguments at key [com.airbnb.mvrx.MvRx.KEY_ARG].
     */
    constructor(args: KnowledgeArgs) : this(id = args.id)
}

private const val FIRST_PAGE = 0

class KnowledgeViewModel(state: KnowledgeState, private val apiService: ApiService) :
    MvRxViewModel<KnowledgeState>(state) {
    init {
        fetchKnowLedge()
    }

    fun fetchKnowLedge() {
        withState { state ->
            if (state.request is Loading) return@withState //避免重复请求

            apiService.fetchKnowledgeList(FIRST_PAGE, cid = state.id)
                .map { HttpResult(it.data.datas) }
                .execute {
                    copy(
                        request = it,
                        articles = (it()?.data ?: emptyList()),
                        page = state.page + 1
                    )
                }
        }
    }

    fun fetchNextPage() {
        withState { state ->
            if (state.request is Loading) return@withState //避免重复请求

            apiService.fetchKnowledgeList(page = state.page, cid = state.id)
                .map { HttpResult(it.data.datas) }
                .execute {
                    copy(
                        request = it,
                        articles = articles + (it()?.data ?: emptyList()),
                        page = state.page + 1
                    )
                }
        }
    }


    companion object : MvRxViewModelFactory<KnowledgeViewModel, KnowledgeState> {

        override fun create(
            viewModelContext: ViewModelContext,
            state: KnowledgeState
        ): KnowledgeViewModel {
            val service: ApiService by viewModelContext.activity.inject()
            return KnowledgeViewModel(state, service)
        }
    }
}

class KnowledgeFragment : BaseFragment() {

    private lateinit var recyclerView: EpoxyRecyclerView

    private val knowledgeViewModel: KnowledgeViewModel by fragmentViewModel()

    companion object {
        fun getInstance(cid: Int): KnowledgeFragment {
            val fragment = KnowledgeFragment()
            val args = KnowledgeArgs(cid)
            val bundle = args.let { Bundle().apply { putParcelable(MvRx.KEY_ARG, it) } }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_knowledge, container, false).apply {
            recyclerView = findViewById(R.id.recycleView)
            recyclerView.setController(epoxyController)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        knowledgeViewModel.asyncSubscribe(
            KnowledgeState::request,
            onSuccess = {
                swipeRefreshLayout.isRefreshing = false
            },
            onFail = { error ->
                swipeRefreshLayout.isRefreshing = false
                Log.w(TAG, "knowledge request failed", error)
            }
        )
        swipeRefreshLayout.setOnRefreshListener(knowledgeViewModel::fetchKnowLedge)
    }

    override fun epoxyController(): MvRxEpoxyController =
        simpleController(knowledgeViewModel) { knowledgeState ->
            knowledgeState.articles.forEach { art ->
                articleRow {
                    id(art.id)
                    article(art)
                    clickListener { _ ->
                        navigateTo(
                            R.id.action_to_webview_fragment,
                            WebViewDetailArgs(art.link, art.title)
                        )
                    }
                }
            }

            loadingRow {
                // Changing the ID will force it to rebind when new data is loaded even if it is
                // still on screen which will ensure that we trigger loading again.
                id("loading${knowledgeState.page}")
                onBind { _, _, _ -> knowledgeViewModel.fetchNextPage() }
            }
        }
}