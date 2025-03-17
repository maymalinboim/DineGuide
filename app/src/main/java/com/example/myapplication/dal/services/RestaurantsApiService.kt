package com.example.myapplication.dal.services

import com.example.myapplication.models.Restaurant
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RestaurantsApiService {
    @GET("discover/restaurant")
    suspend fun discoverRestaurants(
        @Query("page") page: Int = 1,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("include_video") includeVideo: Boolean = false,
        @Query("language") language: String = "en-US"
    ): RestaurantsDTO

    @GET("restaurant/{id}")
    suspend fun getRestaurantById(@Path("id") id: Int): Restaurant

    companion object {
        fun create(): RestaurantsApiService {
            return NetworkModule().retrofit.create(RestaurantsApiService::class.java)
        }
    }
}