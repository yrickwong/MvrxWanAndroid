package com.yrickwong.tech.pictureapp.bean

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HttpResult<T>(
    @Json(name = "list") val list: T
)


@JsonClass(generateAdapter = true)
data class Picture(
    @Json(name = "title") val title: String,
    @Json(name = "thumb") val thumb: String
)