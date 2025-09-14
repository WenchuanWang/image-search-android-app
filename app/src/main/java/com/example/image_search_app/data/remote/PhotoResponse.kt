package com.example.image_search_app.data.remote

import com.squareup.moshi.Json

data class PhotoResponse(
    @Json(name = "photos")
    val photos: PhotoListResponse,
)

data class PhotoListResponse(
    @Json(name = "page")
    val page: Int,
    @Json(name = "pages")
    val pages: Int,
    @Json(name = "perpage")
    val perpage: Int,
    @Json(name = "total")
    val total: Int,
    @Json(name = "photo")
    val items: List<PhotoItemResponse>
)

data class PhotoItemResponse(
    @Json(name = "id")
    val id: String,
    @Json(name = "owner")
    val owner: String,
    @Json(name = "secret")
    val secret: String,
    @Json(name = "server")
    val server: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "farm")
    val farm: Int,
    @Json(name = "ispublic")
    val ispublic: Int,
    @Json(name = "isfriend")
    val isfriend: Int,
    @Json(name = "isfamily")
    val isfamily: Int
)
