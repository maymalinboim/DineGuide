package com.example.myapplication.dal.services

import com.example.myapplication.models.Restaurant
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RestaurantsApiService {
    @GET("restaurant/{id}")
    suspend fun getRestaurantById(@Path("id") id: String): Restaurant

    companion object {
        fun create(): RestaurantsApiService {
            return NetworkModule().retrofit.create(RestaurantsApiService::class.java)
        }
    }
}