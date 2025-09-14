package com.example.image_search_app.di

import com.example.image_search_app.data.remote.FlickrInterceptor
import com.example.image_search_app.data.remote.ImageSearchApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideBaseUrl(): String = "https://api.flickr.com/"


    @Provides
    @Singleton
    fun provideOkHttpClient(
        flickrInterceptor: FlickrInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(flickrInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    
    @Provides
    @Singleton
    fun provideRetrofit(
        baseUrl: String,
        client: OkHttpClient,
        moshi: Moshi
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideImageSearchApiService(retrofit: Retrofit): ImageSearchApiService =
        retrofit.create(ImageSearchApiService::class.java)
}
