package com.example.myapplication.ui.authScreens.restaurants

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.dal.repositories.RestaurantRepository
import com.example.myapplication.models.Restaurant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RestaurantsViewModel(private val restaurantRepository: RestaurantRepository) : ViewModel() {
    private val _restaurants = MutableLiveData<List<Restaurant>>()
    val restaurants: LiveData<List<Restaurant>> get() = _restaurants
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        fetchRestaurants()
    }

    private fun fetchRestaurants() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val restaurantList = restaurantRepository.getAllRestaurants()
            _restaurants.postValue(restaurantList)
            withContext(Dispatchers.Main) { _isLoading.value = false }
        }
    }
}