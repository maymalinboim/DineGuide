package com.example.myapplication.dal.repositories

import android.content.Context
import android.util.Log
import com.example.myapplication.dal.room.AppDatabase
import com.example.myapplication.dal.services.RestaurantsApiService
import com.example.myapplication.models.Restaurant
import com.example.myapplication.models.Review
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class RestaurantRepository(private val context: Context) {
    companion object {
        const val RESTAURANTS_COLLECTION = "restaurants"
    }

    private val db = FirebaseFirestore.getInstance()
    private val localDb = AppDatabase.getDatabase(context)

    suspend fun getRestaurantById(id: String): Restaurant {
        var restaurant = localDb.restaurantDao().getRestaurantById(id)

        if (restaurant != null) {
            Log.d("getRestaurantById1", restaurant.toString())
            return restaurant
        }

        Log.d("hereee3",id.toString())
        restaurant = getRestaurantFromFireStore(id)
        Log.d("hereee4","")

        Log.d("getRestaurantById2", restaurant.toString())
        localDb.restaurantDao().insertAll(restaurant)

        return restaurant;
    }

    private suspend fun getRestaurantFromFireStore(restaurantId: String): Restaurant {
        val restaurant = db.collection(RESTAURANTS_COLLECTION)
            .document(restaurantId)
            .get()
            .await()
            .toObject(Restaurant::class.java)

        restaurant?.id = restaurantId
        return restaurant!!
    }

    suspend fun getAllRestaurants(): List<Restaurant> {
        val restaurantsRef = db.collection("restaurants")

        val restaurants = restaurantsRef.get().await().documents.map { document ->
            document.toObject(Restaurant::class.java)!!.apply { id = document.id }
        }

        localDb.restaurantDao().insertAll(*restaurants.toTypedArray())
        return restaurants
    }
}