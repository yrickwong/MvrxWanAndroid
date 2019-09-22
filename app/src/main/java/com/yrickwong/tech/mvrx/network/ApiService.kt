package com.yrickwong.tech.mvrx.network

import com.yrickwong.tech.mvrx.bean.Article
import com.yrickwong.tech.mvrx.bean.ArticleList
import com.yrickwong.tech.mvrx.bean.Banner
import com.yrickwong.tech.mvrx.bean.HttpResult
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("/banner/json")
    fun fetchBanner(): Observable<HttpResult<List<Banner>>>


    //获取置顶
    @GET("/article/top/json")
    fun fetchTopArticles(): Observable<HttpResult<List<Article>>>

    /**
     * 获取文章列表
     * http://www.wanandroid.com/article/list/0/json
     * @param pageNum
     */
    @GET("article/list/{pageNum}/json")
    fun getArticles(@Path("pageNum") pageNum: Int): Observable<HttpResult<ArticleList>>
}