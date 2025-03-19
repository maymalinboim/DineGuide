package com.example.myapplication.dal.services

import com.example.myapplication.models.Review
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ReviewsApiService {
    @GET("discover/review")
    suspend fun discoverReviews(
        @Query("page") page: Int = 1,
    ): ReviewDTO

    @GET("review/{id}")
    suspend fun getReviewById(@Path("id") id: String): Review

    companion object {
        fun create(): ReviewsApiService {
            return NetworkModule().retrofit.create(ReviewsApiService::class.java)
        }
    }
}