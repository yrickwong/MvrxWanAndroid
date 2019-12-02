package com.yrickwong.tech.pictureapp.bean

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HttpResult<T>(
    @Json(name = "list") val data: T
)


@JsonClass(generateAdapter = true)
data class Picture(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "thumb") val thumb: String
)