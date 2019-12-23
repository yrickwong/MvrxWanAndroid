package com.yrickwong.tech.pictureapp

import com.yrickwong.tech.pictureapp.bean.HttpResult
import com.yrickwong.tech.pictureapp.bean.Picture
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {

    @Headers("Accept: application/json")
    @GET("/j")
    fun search(
        @Query("q") query: String,
        @Query("sn") page: Int,
        @Query("pn") limit: Int = 10
    ): Single<HttpResult<List<Picture>>>

}