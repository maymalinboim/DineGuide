package com.example.myapplication.dal.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.models.Restaurant

@Dao
interface RestaurantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg restaurant: Restaurant)

    @Query("SELECT * FROM restaurants WHERE id = :restaurantId")
    fun getRestaurantById(restaurantId: String): Restaurant
}