// File: MyApplication/app/src/main/java/com/example/myapplication/ui/components/RestaurantCard.kt

package com.example.myapplication.ui.components

import RestaurantDiffCallback
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.models.Restaurant
import com.example.myapplication.ui.authScreens.restaurants.RestaurantsFragmentDirections


class RestaurantCardAdapter(private var restaurants: List<Restaurant>) :
    RecyclerView.Adapter<RestaurantCardAdapter.RestaurantCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.restaurant_card, parent, false)
        return RestaurantCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantCardViewHolder, position: Int) {
        holder.bind(restaurants[position])
    }

    override fun getItemCount(): Int = restaurants.size

    fun updateRestaurants(newRestaurants: List<Restaurant>) {
        val diffCallback = RestaurantDiffCallback(restaurants, newRestaurants)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        restaurants = newRestaurants
        diffResult.dispatchUpdatesTo(this)
    }

    class RestaurantCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(restaurant: Restaurant) {
            itemView.findViewById<TextView>(R.id.restaurantName).text = restaurant.name
            itemView.findViewById<TextView>(R.id.restaurantDescription).text = restaurant.description
            Glide.with(itemView.context)
//                .load("https://image.tmdb.org/t/p/w500/${restaurant.imagePath}")
                .load(restaurant.imagePath)
                .into(itemView.findViewById(R.id.restaurantImage))

            itemView.findViewById<Button>(R.id.addReviewButton).setOnClickListener {
                val action =
                    RestaurantsFragmentDirections.actionRestaurantsFragmentToAddNewReviewFragment(
                        restaurant.id,
                        ""
                    )
                itemView.findNavController().navigate(action)
            }
        }
    }
}