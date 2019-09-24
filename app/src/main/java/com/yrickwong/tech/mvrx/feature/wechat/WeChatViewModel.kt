package com.yrickwong.tech.mvrx.feature.wechat

import com.airbnb.mvrx.*
import com.yrickwong.tech.mvrx.bean.HttpResult
import com.yrickwong.tech.mvrx.bean.WXChapterBean
import com.yrickwong.tech.mvrx.core.MvRxViewModel
import com.yrickwong.tech.mvrx.network.ApiService
import org.koin.android.ext.android.inject


data class WeChatState(
    val wxChapters: List<WXChapterBean> = emptyList(),
    val request: Async<HttpResult<List<WXChapterBean>>> = Uninitialized
) :
    MvRxState

class WeChatViewModel(
    state: WeChatState,
    private val apiService: ApiService
) : MvRxViewModel<WeChatState>(state) {
    init {
        fetchWxChapter()
    }

    private fun fetchWxChapter() {

        withState { state ->
            if (state.request is Loading) return@withState //避免重复请求

            apiService
                .fetchWXChapters()
                .execute { copy(request = it, wxChapters = (it()?.data ?: emptyList())) }
        }
    }


    companion object : MvRxViewModelFactory<WeChatViewModel, WeChatState> {

        override fun create(
            viewModelContext: ViewModelContext,
            state: WeChatState
        ): WeChatViewModel {
            val service: ApiService by viewModelContext.activity.inject()
            return WeChatViewModel(state, service)
        }
    }
}