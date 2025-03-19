package com.example.myapplication.dal.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.dal.room.UserDao
import com.example.myapplication.dal.room.dao.ImageDao
import com.example.myapplication.dal.room.dao.RestaurantDao
import com.example.myapplication.dal.room.dao.ReviewDao
import com.example.myapplication.models.Image
import com.example.myapplication.models.Restaurant
import com.example.myapplication.models.Review
import com.example.myapplication.models.User

@Database(entities = [User::class, Image::class, Restaurant::class, Review::class], version = 9, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun imageDao(): ImageDao
    abstract fun restaurantDao(): RestaurantDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context?): AppDatabase {
            requireNotNull(context?.applicationContext) { "context must not be null"}

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context?.applicationContext!!,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}