package com.example.myapplication.ui.authScreens.myReviews

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.models.Review
import com.example.myapplication.dal.repositories.ReviewsRepository
import kotlinx.coroutines.launch

class MyReviewsViewModel(private val repository: ReviewsRepository) : ViewModel() {
    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> get() = _reviews

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _editReviewEvent = MutableLiveData<Review?>()
    val editReviewEvent: LiveData<Review?> get() = _editReviewEvent

    private val _deleteReviewEvent = MutableLiveData<Review?>()
    val deleteReviewEvent: LiveData<Review?> get() = _deleteReviewEvent

    init {
        fetchMyReviews()
    }

    private fun fetchMyReviews() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.getMyReviews()

            _reviews.value = result
            _isLoading.value = false
        }
    }

    fun onEditReviewClicked(review: Review) {
        _editReviewEvent.value = review
    }

    fun onDeleteReviewClicked(review: Review) {
        _deleteReviewEvent.value = review
    }

    fun onEditReviewEventHandled() {
        _editReviewEvent.value = null
    }

    fun onDeleteReviewEventHandled() {
        _deleteReviewEvent.value = null
    }

    fun deleteReview(review: Review) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.deleteReview(review.id)
            fetchMyReviews()
        }
    }
}
