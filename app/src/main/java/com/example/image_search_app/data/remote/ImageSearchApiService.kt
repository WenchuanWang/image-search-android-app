package com.example.image_search_app.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface ImageSearchApiService {
    @GET("services/rest/")
    suspend fun getImageList(
        @Query("text") text: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): PhotoResponse
}
