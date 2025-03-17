package com.example.myapplication.dal.repositories

import android.content.Context
import com.example.myapplication.dal.room.AppDatabase
import com.example.myapplication.dal.services.RestaurantsApiService
import com.example.myapplication.models.Restaurant

class RestaurantRepository(private val context: Context) {
    private val apiService: RestaurantsApiService = RestaurantsApiService.create()
    private val localDb = AppDatabase.getDatabase(context)

    suspend fun discoverRestaurants(page: Int = 1): List<Restaurant> {
        val restaurants = apiService.discoverRestaurants(page = page).toRestaurants()
        localDb.restaurantDao().insertAll(*restaurants.toTypedArray())
        return restaurants
    }

    suspend fun getRestaurantById(id: Int): Restaurant {
        var restaurant = localDb.restaurantDao().getRestaurantById(id)

        if (restaurant != null) return restaurant

        restaurant = apiService.getRestaurantById(id)
        localDb.restaurantDao().insertAll(restaurant)

        return restaurant;
    }
}