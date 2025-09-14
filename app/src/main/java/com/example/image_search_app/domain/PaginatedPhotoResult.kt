package com.example.image_search_app.domain

data class PaginatedPhotoResult(
    val photos: List<Photo>,
    val currentPage: Int,
    val totalPages: Int,
    val totalPhotos: Int
) {
    val hasNextPage: Boolean get() = currentPage < totalPages
}

data class Photo(
    val id: String,
    val url: String
)
