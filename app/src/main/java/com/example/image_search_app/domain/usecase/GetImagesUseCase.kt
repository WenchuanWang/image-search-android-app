package com.example.image_search_app.domain.usecase

import com.example.image_search_app.domain.ImageSearchRepository
import com.example.image_search_app.domain.PaginatedPhotoResult
import javax.inject.Inject

class GetImagesUseCase @Inject constructor(
    private val repository: ImageSearchRepository
) {
    suspend operator fun invoke(
        query: String,
        page: Int = 1,
        perPage: Int = 20
    ): Result<PaginatedPhotoResult> {
        return repository.getImageList(
            searchQuery = query,
            page = page,
            perPage = perPage
        )
    }
}
