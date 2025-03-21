package com.example.myapplication.dal.services

import com.example.myapplication.models.Review
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
private const val QUERY = "restaurant review OR food review OR restaurant interior OR dining experience OR restaurant customer feedback OR food critic"

interface ReviewsApiService {
    @GET("search")
    suspend fun searchImages(
        @Query("query") query: String = QUERY,
    ): ReviewsDTO

    @GET("movie/{id}")
    suspend fun getMovieById(@Path("id") id: Int): Review

    companion object {
        fun create(): ReviewsApiService {
            return NetworkModule().retrofit.create(ReviewsApiService::class.java)
        }
    }
}