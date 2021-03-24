package com.yrickwong.tech.pictureapp.feature

import android.text.TextUtils
import android.util.Log
import com.airbnb.mvrx.*
import com.yrickwong.tech.pictureapp.ApiService
import com.yrickwong.tech.pictureapp.PICTURE_PER_PAGE
import com.yrickwong.tech.pictureapp.bean.HttpResult
import com.yrickwong.tech.pictureapp.bean.Picture
import org.koin.android.ext.android.inject

private const val TAG = "wangyi"
private const val DEFAULT_PLACE = "上海"

data class PictureState(
    val place: String? = null,
    val pictures: List<Picture> = emptyList(),
    val request: Async<HttpResult<List<Picture>>> = Uninitialized
) : MavericksState

class PictureViewModel(pictureState: PictureState, private val apiService: ApiService) :
    MavericksViewModel<PictureState>(pictureState) {

    init {
        fetchData(DEFAULT_PLACE)
    }

    fun reloadData() {
        withState { state ->
            Log.d(TAG, "reloadData: place=${state.place}")
            if (state.request is Loading || TextUtils.isEmpty(state.place)) return@withState //避免重复请求

            suspend {
                apiService
                    .search(
                        query = state.place!!,
                        page = 1,
                        limit = PICTURE_PER_PAGE
                    )
            }.execute {
                    copy(
                        request = it,
                        place = place,
                        pictures = (it()?.data ?: emptyList())
                    )
                }
        }
    }

    fun fetchData(place: String) {
        Log.d(TAG, "fetchData: place=$place")
        withState { state ->
            if (state.request is Loading) return@withState //避免重复请求

            suspend {
                apiService
                    .search(
                        query = place,
                        page = 1,
                        limit = PICTURE_PER_PAGE
                    )
            }.execute {
                    copy(
                        request = it,
                        place = place,
                        pictures = (it()?.data ?: emptyList())
                    )
                }
        }
    }

    /**
     * 获取数据
     */
    fun fetchNextPage() {
        Log.d(TAG, "fetchNextPage: ")
        withState { state ->
            if (state.request is Loading) return@withState //避免重复请求

            suspend {
                apiService
                    .search(
                        query = state.place ?: "",
                        page = state.pictures.size / PICTURE_PER_PAGE + 1,
                        limit = PICTURE_PER_PAGE
                    )
            }.execute {
                    copy(
                        request = it,
                        place = place,
                        pictures = pictures + (it()?.data ?: emptyList())
                    )
                }
        }
    }


    companion object : MavericksViewModelFactory<PictureViewModel, PictureState> {

        override fun create(
            viewModelContext: ViewModelContext,
            state: PictureState
        ): PictureViewModel {
            val service: ApiService by viewModelContext.activity.inject()
            return PictureViewModel(state, service)
        }
    }
}