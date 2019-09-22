package com.yrickwong.tech.mvrx.feature.banner

import com.airbnb.mvrx.*
import com.yrickwong.tech.mvrx.bean.Banner
import com.yrickwong.tech.mvrx.bean.HttpResult
import com.yrickwong.tech.mvrx.core.MvRxViewModel
import com.yrickwong.tech.mvrx.network.ApiService
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject


data class BannerState(
    /** We use this request to store the list of all banners. */
    val banners: List<Banner> = emptyList(),
    val request: Async<HttpResult<List<Banner>>> = Uninitialized
) : MvRxState

class BannerViewModel(bannerState: BannerState, private val apiService: ApiService) :
    MvRxViewModel<BannerState>(bannerState) {


    init {
        fetchBanner()
    }

    fun fetchBanner() {
        withState { state ->
            if (state.request is Loading) return@withState //避免重复请求

            apiService
                .fetchBanner()
                .execute { copy(request = it, banners = (it()?.data ?: emptyList())) }
        }
    }

    /**
     * If you implement MvRxViewModelFactory in your companion object, MvRx will use that to create
     * your ViewModel. You can use this to achieve constructor dependency injection with MvRx.
     *
     * @see MvRxViewModelFactory
     */
    companion object : MvRxViewModelFactory<BannerViewModel, BannerState> {

        override fun create(
            viewModelContext: ViewModelContext,
            state: BannerState
        ): BannerViewModel {
            val service: ApiService by viewModelContext.activity.inject()
            return BannerViewModel(state, service)
        }
    }
}