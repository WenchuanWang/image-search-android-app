package com.example.image_search_app.data.mapper

import com.example.image_search_app.data.remote.PhotoItemResponse
import com.example.image_search_app.data.remote.PhotoListResponse
import com.example.image_search_app.data.remote.PhotoResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PhotoMappersTest {

    @Test
    fun `toDomain should map PhotoResponse to PaginatedPhotoResult correctly`() {
        // Given
        val photoItemResponse1 = PhotoItemResponse(
            id = "photo1",
            owner = "owner1",
            secret = "secret1",
            server = "server1",
            title = "Photo 1",
            farm = 1,
            ispublic = 1,
            isfriend = 0,
            isfamily = 0
        )

        val photoItemResponse2 = PhotoItemResponse(
            id = "photo2",
            owner = "owner2",
            secret = "secret2",
            server = "server2",
            title = "Photo 2",
            farm = 2,
            ispublic = 1,
            isfriend = 0,
            isfamily = 0
        )
        
        val photoListResponse = PhotoListResponse(
            page = 2,
            pages = 10,
            perpage = 100,
            total = 1000,
            items = listOf(photoItemResponse1, photoItemResponse2)
        )
        
        val photoResponse = PhotoResponse(photos = photoListResponse)

        // When
        val result = photoResponse.toDomain()

        // Then
        assertEquals(2, result.currentPage)
        assertEquals(10, result.totalPages)
        assertEquals(1000, result.totalPhotos)
        assertTrue(result.hasNextPage)
        assertEquals(2, result.photos.size)
        assertEquals("photo1", result.photos[0].id)
        assertEquals("https://farm1.static.flickr.com/server1/photo1_secret1.jpg", result.photos[0].url)
        assertEquals("photo2", result.photos[1].id)
        assertEquals("https://farm2.static.flickr.com/server2/photo2_secret2.jpg", result.photos[1].url)
    }

    @Test
    fun `toDomain should set hasNextPage to false when on last page`() {
        // Given
        val photoItemResponse = PhotoItemResponse(
            id = "photo123",
            owner = "test_owner",
            secret = "secret123",
            server = "server123",
            title = "Test Photo",
            farm = 1,
            ispublic = 1,
            isfriend = 0,
            isfamily = 0
        )
        
        val photoListResponse = PhotoListResponse(
            page = 10,
            pages = 10,
            perpage = 100,
            total = 1000,
            items = listOf(photoItemResponse)
        )
        
        val photoResponse = PhotoResponse(photos = photoListResponse)

        // When
        val result = photoResponse.toDomain()

        // Then
        assertEquals(10, result.currentPage)
        assertEquals(10, result.totalPages)
        assertFalse(result.hasNextPage)
    }
}
