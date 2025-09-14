package com.example.image_search_app.domain.usecase

import com.example.image_search_app.domain.ImageSearchRepository
import com.example.image_search_app.domain.PaginatedPhotoResult
import com.example.image_search_app.domain.Photo
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetImagesUseCaseTest {

    private lateinit var repository: ImageSearchRepository
    private lateinit var useCase: GetImagesUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetImagesUseCase(repository)
    }

    @Test
    fun `Given repository returns success When invoke is called Then paginated result is returned`() = runTest {
        // Given
        val query = "cats"
        val page = 1
        val perPage = 20
        val photos = listOf(
            Photo("photo1", "https://example.com/photo1.jpg"),
            Photo("photo2", "https://example.com/photo2.jpg")
        )
        val expectedResult = PaginatedPhotoResult(
            photos = photos,
            currentPage = page,
            totalPages = 5,
            totalPhotos = 100
        )

        coEvery { repository.getImageList(query, page, perPage) } returns Result.success(expectedResult)

        // When
        val result = useCase(query, page, perPage)

        // Then
        assertTrue(result.isSuccess)
        val actualResult = result.getOrThrow()
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `Given repository returns error When invoke is called Then error is propagated`() = runTest {
        // Given
        val query = "cats"
        val page = 1
        val perPage = 20
        val exception = RuntimeException("Network error")

        coEvery { repository.getImageList(query, page, perPage) } returns Result.failure(exception)

        // When
        val result = useCase(query, page, perPage)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `Given default parameters When invoke is called Then default values are used`() = runTest {
        // Given
        val query = "cats"
        val photos = listOf(Photo("photo1", "https://example.com/photo1.jpg"))
        val expectedResult = PaginatedPhotoResult(
            photos = photos,
            currentPage = 1,
            totalPages = 1,
            totalPhotos = 1
        )

        coEvery { repository.getImageList(query, 1, 20) } returns Result.success(expectedResult)

        // When
        val result = useCase(query)

        // Then
        assertTrue(result.isSuccess)
        val actualResult = result.getOrThrow()
        assertEquals(expectedResult, actualResult)
    }
}
