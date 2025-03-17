package com.example.myapplication.dal.services

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkModule {

    companion object {
        private const val BASE_URL = "https://api.themoviedb.org/3/"
        private const val BEARER_TOKEN =
            "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI1NGMxZGYwYmZiYTRiYTMyMTVlMDY0NDJlZDVhODRmNSIsInN1YiI6IjY2MjNiOTgyMmUyYjJjMDE4NzY2MjkxNCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.ZoOoGJkG8Q0s0iDo81dda5qivTjBCV7TokWNP8r7shE"
    }

    private val authInterceptor = Interceptor { chain ->
        val newRequest: Request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $BEARER_TOKEN")
            .addHeader("accept", "application/json")
            .build()
        chain.proceed(newRequest)
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


}