package com.yrickwong.tech.mvrx.network

import com.yrickwong.tech.mvrx.bean.*
import io.reactivex.Observable
import retrofit2.http.*

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
    @GET("/article/list/{pageNum}/json")
    fun getArticles(@Path("pageNum") pageNum: Int): Observable<HttpResult<ArticleList>>

    /**
     * 获取公众号列表
     * http://wanandroid.com/wxarticle/chapters/json
     */
    @GET("/wxarticle/chapters/json")
    fun fetchWXChapters(): Observable<HttpResult<List<WXChapterBean>>>


    /**
     * 知识体系下的文章
     * http://www.wanandroid.com/article/list/0/json?cid=168
     * @param page
     * @param cid
     */
    @GET("/article/list/{page}/json")
    fun fetchKnowledgeList(@Path("page") page: Int, @Query("cid") cid: Int): Observable<HttpResult<ArticleList>>


    /**
     * 登录
     * http://www.wanandroid.com/user/login
     * @param username
     * @param password
     */
    @POST("/user/login")
    @FormUrlEncoded
    fun signIn(@Field("username") username: String, @Field("password") password: String): Observable<HttpResult<Account>>
}
