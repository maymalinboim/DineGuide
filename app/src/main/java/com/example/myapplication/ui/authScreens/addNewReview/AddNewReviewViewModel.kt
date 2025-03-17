package com.example.myapplication.ui.authScreens.addNewReview

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.dal.repositories.RestaurantRepository
import com.example.myapplication.dal.repositories.ReviewsRepository
import com.example.myapplication.models.Restaurant
import com.example.myapplication.models.Review
import com.example.myapplication.utils.Validator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddNewReviewViewModel(
    private val reviewsRepository: ReviewsRepository,
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val validator = Validator()

    val title = MutableLiveData("")
    val content = MutableLiveData("")
    val imageUri = MutableLiveData("")
    val restaurant = MutableLiveData<Restaurant>()
    val restaurantName = MutableLiveData("")

    private var reviewId: String? = ""

    val isTitleValid = MutableLiveData(true)
    val isContentValid = MutableLiveData(true)
    val isImageUrlValid = MutableLiveData(true)
    val isLoading = MutableLiveData(false)

    val isFormValid: Boolean
        get() = isTitleValid.value!! && isContentValid.value!! && isImageUrlValid.value!!


    fun fetchRestaurant(restaurantId: Int) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newRestaurant = restaurantRepository.getRestaurantById(restaurantId)
                withContext(Dispatchers.Main) {
                    restaurant.value = newRestaurant
                    restaurantName.value = newRestaurant.name
                }
            } catch (e: Exception) {
                Log.e("AddNewReview", "Error fetching restaurant", e)
            } finally {
                withContext(Dispatchers.Main) { isLoading.value = false }
            }
        }
    }

    fun fetchReview(reviewId: String) {
        isLoading.value = true
        this.reviewId = reviewId
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val review = reviewsRepository.getReviewById(reviewId)
                withContext(Dispatchers.Main) {
                    title.value = review.title
                    content.value = review.content
                    imageUri.value = review.imageUri
                }
            } catch (e: Exception) {
                Log.e("AddNewReview", "Error fetching review", e)
            } finally {
                withContext(Dispatchers.Main) { isLoading.value = false }
            }
        }
    }

    fun saveReview(onSuccess: () -> Unit, onFailure: (error: Exception?) -> Unit) {
        validateForm()

        if (!isFormValid) {
            onFailure(null)
            return
        }

        try {
            saveReviewConcurrently(onSuccess, onFailure)
        } catch (e: Exception) {
            Log.e("AddNewReview", "Error saving review", e)
            onFailure(e)
        }
    }

    private fun saveReviewConcurrently(
        onSuccess: () -> Unit, onFailure: (error: Exception?) -> Unit
    ) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val review = constructReviewFromFields()
                val newReview = reviewsRepository.saveReviewInDB(review)
                reviewsRepository.uploadReviewImage(review.imageUri!!, newReview.id)
                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                Log.e("AddNewReview", "Error saving review", e)
                withContext(Dispatchers.Main) { onFailure(e) }
            } finally {
                withContext(Dispatchers.Main) { isLoading.value = false }
            }
        }
    }

    private fun validateForm() {
        isTitleValid.value = validator.validateTitle(title.value!!)
        isContentValid.value = validator.validateContent(content.value!!)
        isImageUrlValid.value = validator.validateImageUri(imageUri.value!!)
    }

    private fun constructReviewFromFields(): Review {
        val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
        val review = Review(
            userId = userId,
            title = title.value!!,
            content = content.value!!,
            timestamp = System.currentTimeMillis(),
            restaurantId = restaurant.value!!.id,
            id = reviewId ?: ""
        )

        return review.apply { imageUri = this@AddNewReviewViewModel.imageUri.value?.let {
            if (!it.startsWith("file:///")) "file://$it" else it
        }  }
    }
}