package com.yrickwong.tech.mvrx.feature.article

import com.airbnb.mvrx.*
import com.yrickwong.tech.mvrx.bean.Article
import com.yrickwong.tech.mvrx.bean.ArticleList
import com.yrickwong.tech.mvrx.bean.HttpResult
import com.yrickwong.tech.mvrx.core.MvRxViewModel
import com.yrickwong.tech.mvrx.network.ApiService
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import org.koin.android.ext.android.inject

private const val FIRST_PAGE = 0

data class ArticleState(
    val articles: List<Article> = emptyList(),
    val page: Int = FIRST_PAGE,
    /** We use this request to store the list of all articles. */
    val request: Async<HttpResult<List<Article>>> = Uninitialized
) : MvRxState


class ArticleViewModel(state: ArticleState, private val apiService: ApiService) :
    MvRxViewModel<ArticleState>(state) {

    //ArticleViewModel 在Fragment的onCreate方法中进行获取
    init {
        fetchArticle()
    }

    fun fetchArticle() {
        withState { state ->
            if (state.request is Loading) return@withState //避免重复请求
            val top: Observable<HttpResult<List<Article>>> = apiService.fetchTopArticles()
            val page: Observable<HttpResult<ArticleList>> = apiService.getArticles(FIRST_PAGE)
            Observable.zip(top, page,
                BiFunction<HttpResult<List<Article>>, HttpResult<ArticleList>, HttpResult<List<Article>>> { t1, t2 ->
                    convert(t1, t2)
                }).execute {
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

            apiService.getArticles(pageNum = state.page).map {
                HttpResult(it.data?.datas)
            }.execute {
                copy(
                    request = it,
                    articles = articles + (it()?.data ?: emptyList()),
                    page = state.page + 1
                )
            }
        }
    }

    private fun convert(
        top: HttpResult<List<Article>>,
        page: HttpResult<ArticleList>
    ): HttpResult<List<Article>> {
        return HttpResult(top.data?.plus(page.data!!.datas))
    }

    companion object : MvRxViewModelFactory<ArticleViewModel, ArticleState> {

        override fun create(
            viewModelContext: ViewModelContext,
            state: ArticleState
        ): ArticleViewModel {
            val service: ApiService by viewModelContext.activity.inject()
            return ArticleViewModel(state, service)
        }
    }
}