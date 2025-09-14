package com.example.image_search_app.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.image_search_app.R
import com.example.image_search_app.domain.Photo
import com.example.image_search_app.presentation.ImageSearchViewModel
import com.example.image_search_app.presentation.ui.components.ScreenStateBox
import com.example.image_search_app.presentation.ui.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPhotoScreen(
    viewModel: ImageSearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_your_photos)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            SearchBar(
                query = uiState.query,
                onQueryChange = viewModel::updateSearchQuery,
                onQueryClear = viewModel::clearPhotoList,
                modifier = Modifier.fillMaxWidth()
            )

            when {
                uiState.isLoading -> ScreenStateBox {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }

                uiState.error != null -> ScreenStateBox {
                    Text(
                        text = uiState.error ?: stringResource(R.string.error_general),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }

                else ->
                    PhotoContent(
                        photos = uiState.items,
                        currentPage = uiState.currentPage,
                        totalPages = uiState.totalPages,
                        totalPhotos = uiState.totalPhotos,
                        hasNextPage = uiState.hasNextPage,
                        isLoadingMore = uiState.isLoadingMore,
                        onLoadMore = viewModel::loadMorePhotos
                    )
            }
        }
    }
}

@Composable
private fun PhotoContent(
    photos: List<Photo>,
    currentPage: Int,
    totalPages: Int,
    totalPhotos: Int,
    hasNextPage: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Pagination info
        if (photos.isNotEmpty()) {
            Text(
                text = stringResource(R.string.page_info, currentPage, totalPages, totalPhotos),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(photos) { photo ->
                PhotoCard(photo)
            }

            // Load more section
            if (hasNextPage) {
                item(span = { GridItemSpan(3) }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isLoadingMore) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            OutlinedButton(
                                onClick = onLoadMore,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.button_load_more))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoCard(
    photo: Photo
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RectangleShape)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(photo.url)
                .crossfade(true)
                .build(),
            contentDescription = "",
            modifier = Modifier
                .fillMaxSize()
                .clip(RectangleShape),
            onLoading = { isLoading = true },
            onSuccess = { isLoading = false },
            onError = {
                isLoading = false
                isError = true
            }
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.dp
            )
        }

        if (isError) {
            Text(
                text = "⚠",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}
