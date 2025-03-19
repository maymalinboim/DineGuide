package com.example.myapplication.ui.authScreens.feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.dal.repositories.ImageRepository
import com.example.myapplication.dal.repositories.RestaurantRepository
import com.example.myapplication.dal.repositories.ReviewsRepository
import com.example.myapplication.dal.repositories.UserRepository
import com.example.myapplication.models.PopulatedReview
import com.example.myapplication.models.Review
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.models.Restaurant
import com.example.myapplication.models.User

class FeedViewModel(
    private val isMyReviews: Boolean,
    private val reviewsRepository: ReviewsRepository,
    private val imageRepository: ImageRepository,
    private val userRepository: UserRepository,
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {
    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> = _reviews

    init {
        Log.d("isMyReviews", isMyReviews.toString())
        fetchReviews()
    }

    fun fetchReviews() {
        viewModelScope.launch(Dispatchers.IO) {
            val reviewsList: List<Review> = reviewsRepository.getAllReviews(isMyReviews)
            _reviews.postValue(reviewsList)
        }
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reviewsRepository.deleteReview(reviewId)
        }
    }

    fun getPopulatedReview(
        review: Review,
        onPopulatedReviewFetched: (populatedReview: PopulatedReview) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userRepository.getUserById(review.userId)
            val restaurant = restaurantRepository.getRestaurantById(review.restaurantId)
            val imageUri = imageRepository.getImagePathById(review.id)

            withContext(Dispatchers.Main) {
                onPopulatedReviewFetched(
                    PopulatedReview(
                        review.apply { this.imageUri = imageUri },
                        user,
                        restaurant
                    )
                )
            }
        }
    }
}