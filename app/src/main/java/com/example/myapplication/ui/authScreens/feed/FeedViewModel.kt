package com.example.myapplication.ui.authScreens.feed

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
import android.util.Log

class FeedViewModel(
    private val isMyReviews: Boolean,
    private val reviewsRepository: ReviewsRepository,
    private val imageRepository: ImageRepository,
    private val userRepository: UserRepository,
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {
    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> = _reviews
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        fetchReviews()
    }

    fun fetchReviews() {
        viewModelScope.launch(Dispatchers.IO) {
            val reviewsList: List<Review> = reviewsRepository.getAllReviews(isMyReviews)
            if (isMyReviews) {
                _reviews.postValue(reviewsList)
            }
            else {
                val generatedReviewList: List<Review> = reviewsRepository.discoverReviews()
                _reviews.postValue(reviewsList + generatedReviewList)
            }
        }
    }

    fun loadMoreReviews() {
        if (_isLoading.value == true || isMyReviews) return

        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val currentReviews = _reviews.value ?: emptyList()
            val reviewsList = reviewsRepository.discoverReviews(page = currentReviews.size / 15 + 1)
            if (reviewsList.isNotEmpty()) {
                _reviews.postValue(currentReviews + reviewsList)
            }
            withContext(Dispatchers.Main) { _isLoading.value = false }
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