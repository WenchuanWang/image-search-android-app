package com.example.image_search_app.data

import com.example.image_search_app.data.remote.ImageSearchApiService
import com.example.image_search_app.data.remote.PhotoItemResponse
import com.example.image_search_app.data.remote.PhotoListResponse
import com.example.image_search_app.data.remote.PhotoResponse
import com.example.image_search_app.domain.ImageSearchRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException

class ImageSearchRepositoryImplTest {

    private lateinit var repository: ImageSearchRepository
    private lateinit var mockApiService: ImageSearchApiService

    @Before
    fun setUp() {
        mockApiService = mockk()
        repository = ImageSearchRepositoryImpl(mockApiService)
    }

    @Test
    fun `Given API returns photos When getImageList is called Then success with paginated result should be returned`() = runTest {
        // Given
        val searchQuery = "cats"
        val page = 1
        val perPage = 100
        val testPhotos = listOf(
            createTestPhotoItemResponse("photo1"),
            createTestPhotoItemResponse("photo2")
        )
        val apiResponse = createTestPhotoResponse(testPhotos, page, 5, perPage, 500)

        coEvery { mockApiService.getImageList(searchQuery, page, perPage) } returns apiResponse

        // When
        val result = repository.getImageList(searchQuery, page, perPage)

        // Then
        assertTrue(result.isSuccess)
        val paginatedResult = result.getOrNull()!!
        assertEquals(2, paginatedResult.photos.size)
        assertEquals("photo1", paginatedResult.photos[0].id)
        assertEquals("photo2", paginatedResult.photos[1].id)
        assertEquals("https://farm1.static.flickr.com/test_server/photo1_test_secret.jpg", paginatedResult.photos[0].url)
        assertEquals("https://farm1.static.flickr.com/test_server/photo2_test_secret.jpg", paginatedResult.photos[1].url)
        assertEquals(1, paginatedResult.currentPage)
        assertEquals(5, paginatedResult.totalPages)
        assertEquals(500, paginatedResult.totalPhotos)
        assertTrue(paginatedResult.hasNextPage)
    }

    @Test
    fun `Given API returns empty photos When getImageList is called Then success with empty result should be returned`() = runTest {
        // Given
        val searchQuery = "nonexistent"
        val page = 1
        val perPage = 100
        val emptyPhotos = emptyList<PhotoItemResponse>()
        val apiResponse = createTestPhotoResponse(emptyPhotos, page, 0, perPage, 0)

        coEvery { mockApiService.getImageList(searchQuery, page, perPage) } returns apiResponse

        // When
        val result = repository.getImageList(searchQuery, page, perPage)

        // Then
        assertTrue(result.isSuccess)
        val paginatedResult = result.getOrNull()!!
        assertTrue(paginatedResult.photos.isEmpty())
        assertEquals(1, paginatedResult.currentPage)
        assertEquals(0, paginatedResult.totalPages)
        assertEquals(0, paginatedResult.totalPhotos)
        assertTrue(!paginatedResult.hasNextPage)
    }

    @Test
    fun `Given API throws exception When getImageList is called Then failure should be returned`() = runTest {
        // Given
        val searchQuery = "error"
        val page = 1
        val perPage = 100

        coEvery { mockApiService.getImageList(searchQuery, page, perPage) } throws UnknownHostException("network issue")

        // When
        val result = repository.getImageList(searchQuery, page, perPage)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is UnknownHostException)
    }

    private fun createTestPhotoResponse(
        photos: List<PhotoItemResponse>,
        page: Int,
        totalPages: Int,
        perPage: Int,
        total: Int
    ): PhotoResponse = PhotoResponse(
        photos = PhotoListResponse(
            page = page,
            pages = totalPages,
            perpage = perPage,
            total = total,
            items = photos
        )
    )

    private fun createTestPhotoItemResponse(
        id: String
    ): PhotoItemResponse = PhotoItemResponse(
        id = id,
        owner = "test_owner",
        secret = "test_secret",
        server = "test_server",
        title = "Test Photo",
        farm = 1,
        ispublic = 1,
        isfriend = 0,
        isfamily = 0
    )
}