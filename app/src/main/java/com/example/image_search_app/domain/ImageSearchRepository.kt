package com.example.image_search_app.domain

interface ImageSearchRepository {
    suspend fun getImageList(
        searchQuery: String, 
        page: Int = 1, 
        perPage: Int = 20
    ): Result<PaginatedPhotoResult>
}
