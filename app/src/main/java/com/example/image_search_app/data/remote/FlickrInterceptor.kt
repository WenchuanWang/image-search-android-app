package com.example.image_search_app.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class FlickrInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalUrl = original.url

        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("api_key", "96358825614a5d3b1a1c3fd87fca2b47")
            .addQueryParameter("method", "flickr.photos.search")
            .addQueryParameter("format", "json")
            .addQueryParameter("nojsoncallback", "1")
            .build()

        val newRequest = original.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}
