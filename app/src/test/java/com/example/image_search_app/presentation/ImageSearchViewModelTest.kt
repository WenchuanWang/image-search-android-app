package com.example.image_search_app.presentation

import com.example.image_search_app.MainDispatcherRule
import com.example.image_search_app.domain.ImageSearchRepository
import com.example.image_search_app.domain.PaginatedPhotoResult
import com.example.image_search_app.domain.Photo
import com.example.image_search_app.domain.usecase.GetImagesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ImageSearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: ImageSearchRepository
    private lateinit var getImagesUseCase: GetImagesUseCase
    private lateinit var viewModel: ImageSearchViewModel

    @Before
    fun setUp() {
        repository = mockk()
        getImagesUseCase = mockk()
        
        // Mock the use case to delegate to repository for testing
        coEvery { getImagesUseCase(any(), any(), any()) } coAnswers {
            val query = firstArg<String>()
            val page = secondArg<Int>()
            val perPage = thirdArg<Int>()
            repository.getImageList(query, page, perPage)
        }
        
        viewModel = ImageSearchViewModel(getImagesUseCase)
    }

    @Test
    fun `Given non-empty query When debounce elapses Then state emits loading then success`() = runTest {
        // Given
        val testPhotos = listOf(
            createTestPhoto("photo1", "https://example.com/photo1.jpg"),
            createTestPhoto("photo2", "https://example.com/photo2.jpg")
        )
        val paginatedResult = PaginatedPhotoResult(
            photos = testPhotos,
            currentPage = 1,
            totalPages = 5,
            totalPhotos = 100
        )

        coEvery { getImagesUseCase("cats", 1, 20) } coAnswers {
            // add a tiny delay so we can observe the loading state
            delay(1)
            Result.success(paginatedResult)
        }

        // When: type query
        viewModel.updateSearchQuery("cats")

        // Before debounce window: nothing started
        advanceTimeBy(299)
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.items.isEmpty())

        // Cross debounce boundary -> loading should flip true
        advanceTimeBy(2)
        assertTrue(viewModel.uiState.value.isLoading)

        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.items.size)
        assertEquals(1, state.currentPage)
        assertEquals(5, state.totalPages)
        assertTrue(state.hasNextPage)
        assertNull(state.error)
    }

    @Test
    fun `Given rapid successive queries When debounce elapses Then repository is called once with latest query`() = runTest {
        // Given
        val testPhotos = listOf(
            createTestPhoto("photo1", "https://example.com/photo1.jpg"),
        )
        val paginatedResult = PaginatedPhotoResult(
            photos = testPhotos,
            currentPage = 1,
            totalPages = 5,
            totalPhotos = 100
        )
        coEvery { getImagesUseCase(any(), any(), any()) } returns Result.success(paginatedResult)

        // When: rapid typing within debounce window
        viewModel.updateSearchQuery("c")
        viewModel.updateSearchQuery("ca")
        viewModel.updateSearchQuery("cat")

        // Only one call should happen after the 300ms window with "cat"
        advanceTimeBy(300)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { getImagesUseCase("cat", 1, 20) }
        assertEquals("cat", viewModel.uiState.value.query)
    }

    @Test
    fun `Given empty query When ViewModel is initialized Then state should be empty`() = runTest {
        // When - wait for VM has been initialised
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.query.isEmpty())
        assertTrue(state.items.isEmpty())
        assertFalse(state.isLoading)
        assertFalse(state.isLoadingMore)
        assertEquals(1, state.currentPage)
        assertEquals(0, state.totalPages)
        assertEquals(0, state.totalPhotos)
        assertFalse(state.hasNextPage)
        assertTrue(state.error == null)
    }

    @Test
    fun `Given valid search query When updateSearchQuery is called Then photos should be loaded`() = runTest {
        // Given
        val searchQuery = "cats"
        val testPhotos = listOf(
            createTestPhoto("photo1", "https://example.com/photo1.jpg"),
            createTestPhoto("photo2", "https://example.com/photo2.jpg")
        )
        val paginatedResult = PaginatedPhotoResult(
            photos = testPhotos,
            currentPage = 1,
            totalPages = 5,
            totalPhotos = 100
        )

        coEvery { getImagesUseCase(searchQuery, 1, 20) } returns Result.success(paginatedResult)

        // When
        viewModel.updateSearchQuery(searchQuery)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(searchQuery, state.query)
        assertEquals(2, state.items.size)
        assertEquals("photo1", state.items[0].id)
        assertEquals("photo2", state.items[1].id)
        assertEquals("https://example.com/photo1.jpg", state.items[0].url)
        assertEquals("https://example.com/photo2.jpg", state.items[1].url)
        assertFalse(state.isLoading)
        assertFalse(state.isLoadingMore)
        assertEquals(1, state.currentPage)
        assertEquals(5, state.totalPages)
        assertEquals(100, state.totalPhotos)
        assertTrue(state.hasNextPage)
        assertTrue(state.error == null)
    }

    @Test
    fun `Given repository returns error When updateSearchQuery is called Then error should be set`() = runTest {
        // Given
        val searchQuery = "error"
        val errorMessage = "Network error"

        coEvery { getImagesUseCase(searchQuery, 1, 20) } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.updateSearchQuery(searchQuery)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(searchQuery, state.query)
        assertTrue(state.items.isEmpty())
        assertFalse(state.isLoading)
        assertFalse(state.isLoadingMore)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `Given hasNextPage is true When loadMorePhotos is called Then more photos should be loaded`() = runTest {
        // Given
        val searchQuery = "cats"
        val initialPhotos = listOf(createTestPhoto("photo1", "https://example.com/photo1.jpg"))
        val morePhotos = listOf(createTestPhoto("photo2", "https://example.com/photo2.jpg"))
        
        val initialResult = PaginatedPhotoResult(
            photos = initialPhotos,
            currentPage = 1,
            totalPages = 5,
            totalPhotos = 100
        )
        val moreResult = PaginatedPhotoResult(
            photos = morePhotos,
            currentPage = 2,
            totalPages = 5,
            totalPhotos = 100
        )

        coEvery { getImagesUseCase(searchQuery, 1, 20) } returns Result.success(initialResult)
        coEvery { getImagesUseCase(searchQuery, 2, 20) } returns Result.success(moreResult)

        // When
        viewModel.updateSearchQuery(searchQuery)
        advanceUntilIdle()
        viewModel.loadMorePhotos()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(2, state.items.size)
        assertEquals("photo1", state.items[0].id)
        assertEquals("photo2", state.items[1].id)
        assertEquals(2, state.currentPage)
        assertTrue(state.hasNextPage)
        assertFalse(state.isLoadingMore)
    }

    @Test
    fun `Given hasNextPage is false When loadMorePhotos is called Then no additional photos should be loaded`() = runTest {
        // Given
        val searchQuery = "cats"
        val testPhotos = listOf(createTestPhoto("photo1", "https://example.com/photo1.jpg"))
        val paginatedResult = PaginatedPhotoResult(
            photos = testPhotos,
            currentPage = 5,
            totalPages = 5,
            totalPhotos = 100
        )

        coEvery { getImagesUseCase(searchQuery, any(), any()) } returns Result.success(paginatedResult)

        // When
        viewModel.updateSearchQuery(searchQuery)
        advanceUntilIdle()
        viewModel.loadMorePhotos()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(5, state.currentPage)
        assertFalse(state.hasNextPage)
        coVerify(exactly = 0) { getImagesUseCase("cats", 5, 20) }
    }

    @Test
    fun `Given clearPhotoList is called When there are photos Then state should be cleared`() = runTest {
        // Given
        val searchQuery = "cats"
        val testPhotos = listOf(createTestPhoto("photo1", "https://example.com/photo1.jpg"))
        val paginatedResult = PaginatedPhotoResult(
            photos = testPhotos,
            currentPage = 1,
            totalPages = 5,
            totalPhotos = 100
        )

        coEvery { getImagesUseCase(searchQuery, 1, 20) } returns Result.success(paginatedResult)

        // When
        viewModel.updateSearchQuery(searchQuery)
        advanceUntilIdle()
        viewModel.clearPhotoList()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.query.isEmpty())
        assertTrue(state.items.isEmpty())
        assertFalse(state.isLoading)
        assertFalse(state.isLoadingMore)
        assertEquals(1, state.currentPage)
        assertEquals(0, state.totalPages)
        assertEquals(0, state.totalPhotos)
        assertFalse(state.hasNextPage)
        assertTrue(state.error == null)
    }

    private fun createTestPhoto(id: String, url: String): Photo = Photo(id = id, url = url)
}