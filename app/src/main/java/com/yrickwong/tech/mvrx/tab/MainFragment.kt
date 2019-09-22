package com.yrickwong.tech.mvrx.tab

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.yrickwong.tech.mvrx.R
import com.yrickwong.tech.mvrx.core.BaseFragment
import com.yrickwong.tech.mvrx.core.simpleController
import com.yrickwong.tech.mvrx.feature.article.ArticleState
import com.yrickwong.tech.mvrx.feature.article.ArticleViewModel
import com.yrickwong.tech.mvrx.feature.banner.BannerState
import com.yrickwong.tech.mvrx.feature.banner.BannerViewModel
import com.yrickwong.tech.mvrx.feature.webview.WebViewDetailArgs
import com.yrickwong.tech.mvrx.views.BannerRowModel_
import com.yrickwong.tech.mvrx.views.articleRow
import com.yrickwong.tech.mvrx.views.carouselPageSnap
import com.yrickwong.tech.mvrx.views.loadingRow
import kotlinx.android.synthetic.main.fragment_main.*


const val TAG = "MainFragment"

class MainFragment : BaseFragment() {

    private lateinit var recyclerView: EpoxyRecyclerView

    private val bannerViewModel: BannerViewModel by activityViewModel()//定义成Activity说明可以再fragment中间进行数据传递复用

    private val articleViewModel: ArticleViewModel by activityViewModel()//会在onCreate的时候进行懒加载创建

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_main, container, false).apply {
            recyclerView = findViewById(R.id.recycleView)
            recyclerView.setController(epoxyController)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bannerViewModel.asyncSubscribe(BannerState::request,
            onSuccess = {
                swipeRefreshLayout.isRefreshing = false
            },
            onFail = { error ->
                swipeRefreshLayout.isRefreshing = false
                Log.w(TAG, "banner request failed", error)
            }
        )
        articleViewModel.asyncSubscribe(ArticleState::request,
            onSuccess = {
                swipeRefreshLayout.isRefreshing = false
            },
            onFail = { error ->
                swipeRefreshLayout.isRefreshing = false
                Log.w(TAG, "article request failed", error)
            }
        )
        swipeRefreshLayout.setOnRefreshListener {
            bannerViewModel.fetchBanner()
            articleViewModel.fetchArticle()
        }
    }

    override fun invalidate() {
        super.invalidate()
        withState(bannerViewModel, articleViewModel) { bannerState, articleState ->
            loadingView.isVisible =
                bannerState.request is Loading || articleState.request is Loading
        }
    }

    override fun epoxyController() =
        simpleController(bannerViewModel, articleViewModel) { bannerState, articleState ->

            carouselPageSnap {
                id("carousel")
                models(mutableListOf<BannerRowModel_>().apply {
                    bannerState.banners.forEach { banner ->
                        add(
                            BannerRowModel_()
                                .id(banner.id)
                                .banner(banner)
                                .clickListener { _ ->
                                    navigateTo(
                                        R.id.action_to_webview_fragment,
                                        WebViewDetailArgs(banner.url, banner.title)
                                    )
                                }
                        )
                    }
                })
            }

            articleState.articles.forEach { art ->
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
                id("loading${articleState.page}")
                onBind { _, _, _ -> articleViewModel.fetchNextPage() }
            }
        }
}

fun Context.showToast(text: CharSequence) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()