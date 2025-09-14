package com.example.image_search_app.di

import com.example.image_search_app.data.ImageSearchRepositoryImpl
import com.example.image_search_app.domain.ImageSearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindImageSearchRepository(impl: ImageSearchRepositoryImpl): ImageSearchRepository
}
