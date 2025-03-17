package com.example.myapplication.dal.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.myapplication.dal.room.AppDatabase
import com.example.myapplication.models.Review
import com.example.myapplication.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import androidx.core.net.toUri

class ReviewsRepository(private val context: Context) {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val localDb = AppDatabase.getDatabase(context)
    private val imageRepository = ImageRepository(context)

    // Fetch reviews for the logged-in user
    suspend fun getMyReviews(): List<Review> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        val snapshot = db.collection("reviews")
            .whereEqualTo("userId", userId)
            .get()
            .await()
        return snapshot.toObjects(Review::class.java)
    }

    // Delete a review from Firestore, local DB, and image repository
    suspend fun deleteReview(reviewId: String) {
        db.collection("reviews").document(reviewId).delete().await()
        localDb.reviewDao().deleteReview(reviewId)
        imageRepository.deleteImage(reviewId)
    }

    // Save a review to Firestore and local DB
    suspend fun saveReviewInDB(review: Review): Review {
        val newReview = saveReviewInFireStore(review)

        localDb.reviewDao().insertAll(newReview)

        return newReview.apply { imageUri = review.imageUri }
    }

    // Save a review in Firestore
    private suspend fun saveReviewInFireStore(review: Review): Review {
        val updatedReview = review.copy(
            timestamp = System.currentTimeMillis()
        )
        updatedReview.imageUri = null // Don't save image URI to Firestore (only local)

        val documentRef = if (review.id.isNotEmpty())
            db.collection("reviews").document(review.id)
        else
            db.collection("reviews").document().also { updatedReview.id = it.id }

        documentRef.set(updatedReview.json).await()
        return updatedReview
    }

    // Upload a review image and save it locally
    suspend fun uploadReviewImage(imageUri: String, reviewId: String) {
        imageRepository.uploadImage(imageUri.toUri(), reviewId)
    }

    // Get all cached reviews (from local DB)
    fun getAllCachedReviews(isLoggedUserReviews: Boolean): LiveData<List<Review>> {
        return if (!isLoggedUserReviews) localDb.reviewDao().getAllReviews()
        else localDb.reviewDao().getAllReviewsOfUser(auth.currentUser!!.uid)
    }

    // Fetch all reviews, both local and remote
    suspend fun getAllReviews(isLoggedUserReviews: Boolean): List<Review> {
        val reviewsRef = db.collection("reviews")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        if (isLoggedUserReviews) reviewsRef.whereEqualTo("userId", auth.currentUser!!.uid)

        val reviews = reviewsRef.get().await().documents.map { document ->
            document.toObject(Review::class.java)!!.apply { id = document.id }
        }

        localDb.reviewDao().insertAll(*reviews.toTypedArray())
        return reviews
    }

    // Get a review by ID, either from local DB or Firestore
    suspend fun getReviewById(reviewId: String): Review {
        var review = localDb.reviewDao().getReviewById(reviewId)
            .apply { imageUri = imageRepository.getImagePathById(reviewId) }

        if (review != null) return review

        review = getReviewFromFireStore(reviewId)
        localDb.reviewDao().insertAll(review)

        return review.apply { imageUri = imageRepository.getImagePathById(reviewId) }
    }

    // Fetch a review from Firestore and cache its image
    private suspend fun getReviewFromFireStore(reviewId: String): Review {
        val review = db.collection("reviews")
            .document(reviewId)
            .get()
            .await()
            .toObject(Review::class.java)

        review?.id = reviewId
        review?.imageUri = imageRepository.getImagePathById(reviewId) // Get local image URI

        return review!!
    }
}
