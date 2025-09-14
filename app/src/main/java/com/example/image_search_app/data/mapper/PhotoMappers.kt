package com.example.image_search_app.data.mapper

import com.example.image_search_app.data.remote.PhotoItemResponse
import com.example.image_search_app.data.remote.PhotoListResponse
import com.example.image_search_app.data.remote.PhotoResponse
import com.example.image_search_app.domain.PaginatedPhotoResult
import com.example.image_search_app.domain.Photo

fun PhotoResponse.toDomain(): PaginatedPhotoResult =
    PaginatedPhotoResult(
        photos = photos.toPhotoList(),
        currentPage = photos.page,
        totalPages = photos.pages,
        totalPhotos = photos.total
    )

private fun PhotoListResponse.toPhotoList(): List<Photo> = this.items.map { it.toPhoto() }

private fun PhotoItemResponse.toPhoto() = Photo(
    id = id,
    url = "https://farm$farm.static.flickr.com/$server/${id}_$secret.jpg"
)
