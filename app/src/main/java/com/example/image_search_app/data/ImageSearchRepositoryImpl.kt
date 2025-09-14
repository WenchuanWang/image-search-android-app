package com.example.image_search_app.data

import com.example.image_search_app.data.mapper.toDomain
import com.example.image_search_app.domain.ImageSearchRepository
import com.example.image_search_app.domain.PaginatedPhotoResult
import com.example.image_search_app.data.remote.ImageSearchApiService
import javax.inject.Inject

class ImageSearchRepositoryImpl @Inject constructor(
    private val apiService: ImageSearchApiService
): ImageSearchRepository {

    override suspend fun getImageList(
        searchQuery: String, 
        page: Int, 
        perPage: Int
    ): Result<PaginatedPhotoResult> =
        try {
            val response = apiService.getImageList(searchQuery, page, perPage)
            val paginatedResult = response.toDomain()
            Result.success(paginatedResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
}
