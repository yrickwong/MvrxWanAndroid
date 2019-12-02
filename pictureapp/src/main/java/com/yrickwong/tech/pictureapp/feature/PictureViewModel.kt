package com.yrickwong.tech.pictureapp.feature

import android.util.Log
import com.airbnb.mvrx.*
import com.yrickwong.tech.pictureapp.ApiService
import com.yrickwong.tech.pictureapp.bean.HttpResult
import com.yrickwong.tech.pictureapp.bean.Picture
import com.yrickwong.tech.pictureapp.core.MvRxViewModel
import org.koin.android.ext.android.inject

private const val FIRST_PAGE = 0

private const val TAG = "wangyi"

data class PictureState(
    val page: Int = FIRST_PAGE,
    val place: String = "",
    val pictures: List<Picture> = emptyList(),
    val request: Async<HttpResult<List<Picture>>> = Uninitialized
) : MvRxState

class PictureViewModel(pictureState: PictureState, private val apiService: ApiService) :
    MvRxViewModel<PictureState>(pictureState) {


    /**
     *
     * @param place String  传递进来的数据不可能为null,该判断的在外面都判断了
     */
    fun fetchData(place: String) {
        Log.d(TAG, "fetchData: ")
        withState { state ->
            if (state.request is Loading) return@withState //避免重复请求


            apiService
                .search(query = place, page = state.page)
                .execute {
                    copy(
                        request = it,
                        page = state.page + 1,
                        place = place,
                        pictures = (it()?.list ?: emptyList())
                    )
                }
        }
    }

    fun fetchNextPage() {
        Log.d(TAG, "fetchNextPage: ")
        withState { state ->
            if (state.request is Loading) return@withState //避免重复请求

            apiService
                .search(query = state.place, page = state.page)
                .execute {
                    copy(
                        request = it,
                        place = state.place,
                        page = state.page + 1,
                        pictures = pictures + (it()?.list ?: emptyList())
                    )
                }
        }
    }

    companion object : MvRxViewModelFactory<PictureViewModel, PictureState> {

        override fun create(
            viewModelContext: ViewModelContext,
            state: PictureState
        ): PictureViewModel {
            val service: ApiService by viewModelContext.activity.inject()
            return PictureViewModel(state, service)
        }
    }
}