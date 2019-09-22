package com.yrickwong.tech.mvrx.rxjava

import com.yrickwong.tech.mvrx.bean.Banner
import com.yrickwong.tech.mvrx.bean.HttpResult
import com.yrickwong.tech.mvrx.network.ApiService
import io.reactivex.Observable
import io.reactivex.functions.Function

class RxService(private val apiService: ApiService) {

    fun fetchBanner(): Observable<List<Banner>> = apiService.fetchBanner().map(unwrapData())

}

/**
 * 对网络请求返回的数据类型进行转换，HttpResult<T> -> T
 */
inline fun <reified T> unwrapData() = Function<HttpResult<T>, T> {
    it.data
}