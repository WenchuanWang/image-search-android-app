package com.example.image_search_app.domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PaginatedPhotoResultTest {

    @Test
    fun `hasNextPage should return true when currentPage is less than totalPages`() {
        // Given
        val photos = listOf(Photo("1", "url1"))
        val result = PaginatedPhotoResult(
            photos = photos,
            currentPage = 1,
            totalPages = 5,
            totalPhotos = 100
        )

        // When & Then
        assertTrue(result.hasNextPage)
    }

    @Test
    fun `hasNextPage should return false when currentPage equals totalPages`() {
        // Given
        val photos = listOf(Photo("1", "url1"))
        val result = PaginatedPhotoResult(
            photos = photos,
            currentPage = 5,
            totalPages = 5,
            totalPhotos = 100
        )

        // When & Then
        assertFalse(result.hasNextPage)
    }

    @Test
    fun `hasNextPage should return false when totalPages is zero`() {
        // Given
        val photos = emptyList<Photo>()
        val result = PaginatedPhotoResult(
            photos = photos,
            currentPage = 1,
            totalPages = 0,
            totalPhotos = 0
        )

        // When & Then
        assertFalse(result.hasNextPage)
    }
}
