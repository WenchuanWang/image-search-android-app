@file:OptIn(kotlinx.coroutines.FlowPreview::class)

package com.example.image_search_app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.image_search_app.domain.Photo
import com.example.image_search_app.domain.usecase.GetImagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PhotoSearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val items: List<Photo> = emptyList(),
    val currentPage: Int = 1,
    val totalPages: Int = 0,
    val totalPhotos: Int = 0,
    val hasNextPage: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ImageSearchViewModel @Inject constructor(
    private val getImagesUseCase: GetImagesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PhotoSearchUiState())
    val uiState: StateFlow<PhotoSearchUiState> = _uiState.asStateFlow()

    init {
        observeQuery()
    }

    private fun observeQuery() {
        viewModelScope.launch {
            uiState
                .map { it.query }
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { searchQuery ->
                    if (searchQuery.isBlank()) {
                        _uiState.update {
                            it.copy(
                                items = emptyList(),
                                isLoading = false,
                                isLoadingMore = false,
                                currentPage = 1,
                                totalPages = 0,
                                totalPhotos = 0,
                                hasNextPage = false,
                                error = null
                            )
                        }
                        return@collectLatest
                    }

                    _uiState.update { it.copy(isLoading = true, error = null, currentPage = 1) }
                    val result = getImagesUseCase(
                        query = searchQuery,
                        page = 1
                    )
                    result.onSuccess { paginatedResult ->
                        _uiState.update {
                            it.copy(
                                items = paginatedResult.photos,
                                isLoading = false,
                                currentPage = paginatedResult.currentPage,
                                totalPages = paginatedResult.totalPages,
                                totalPhotos = paginatedResult.totalPhotos,
                                hasNextPage = paginatedResult.hasNextPage
                            )
                        }
                    }.onFailure { e ->
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
                }
        }
    }

    fun updateSearchQuery(searchQuery: String) {
        _uiState.update { it.copy(query = searchQuery) }
    }

    fun clearPhotoList() {
        _uiState.update { it.copy(query = "") }
    }

    fun loadMorePhotos() {
        val currentState = _uiState.value
        if (currentState.isLoadingMore || !currentState.hasNextPage || currentState.query.isBlank()) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true, error = null) }
            val result = getImagesUseCase(
                query = currentState.query,
                page = currentState.currentPage + 1
            )

            result.onSuccess { paginatedResult ->
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        items = currentUiState.items + paginatedResult.photos,
                        isLoadingMore = false,
                        currentPage = paginatedResult.currentPage,
                        hasNextPage = paginatedResult.hasNextPage
                    )
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoadingMore = false, error = e.message) }
            }
        }
    }
}
