package com.example.myapplication.ui.authScreens.feed

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.models.PopulatedReview
import com.example.myapplication.models.Review
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReviewCardAdapter(
    private var reviews: List<Review>,
    private val feedViewModel: FeedViewModel
) : RecyclerView.Adapter<ReviewCardAdapter.ReviewCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_card, parent, false)
        return ReviewCardViewHolder(view, feedViewModel)
    }

    override fun onBindViewHolder(holder: ReviewCardViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount(): Int = reviews.size

    fun updateReviews(newReviews: List<Review>) {
        reviews = newReviews
        notifyDataSetChanged()
    }

    class ReviewCardViewHolder(
        itemView: View,
        private val feedViewModel: FeedViewModel
    ) : RecyclerView.ViewHolder(itemView) {
        private val reviewTitle: TextView = itemView.findViewById(R.id.reviewTitle)
        private val timestamp: TextView = itemView.findViewById(R.id.timestamp)
        private val reviewContent: TextView = itemView.findViewById(R.id.reviewContent)
        private val reviewUser: TextView = itemView.findViewById(R.id.reviewUser)
        private val reviewRestaurant: TextView = itemView.findViewById(R.id.reviewRestaurant)
        private val reviewImage: ImageView = itemView.findViewById(R.id.reviewImage)
        private val progressBar: View = itemView.findViewById(R.id.progress_bar)
        private val deleteReviewButton: View = itemView.findViewById(R.id.deleteReviewButton)
        private val editReviewButton: View = itemView.findViewById(R.id.editReviewButton)

        fun bind(review: Review) {
            setupDeleteReviewButton(review.id)
            setupEditReviewButton(review.id, review.restaurantId)
            setFieldsVisibility(View.GONE, review.userId)
            progressBar.visibility = View.VISIBLE

            if (review.userId == "") {
                setupReviewData(review)
                progressBar.visibility = View.GONE
                setFieldsVisibility(View.VISIBLE, review.userId)
            }
            else {
                feedViewModel.getPopulatedReview(review) { populatedReview ->
                    setupPopulatedReviewData(populatedReview)
                    progressBar.visibility = View.GONE
                    setFieldsVisibility(View.VISIBLE, review.userId)
                }
            }
        }

        private fun convertMillisToDateString(
            millis: Long,
            pattern: String = "MM/dd/yyyy HH:mm"
        ): String {
            val date = Date(millis)
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
            return sdf.format(date)
        }

        private fun setFieldsVisibility(visibility: Int, userId: String) {
            reviewTitle.visibility = visibility
            timestamp.visibility = visibility
            reviewContent.visibility = visibility
            reviewUser.visibility = visibility
            reviewRestaurant.visibility = visibility
            reviewImage.visibility = visibility
            val userButtonsVisibility = if (canShowUserButtons(userId)) visibility else View.GONE
            deleteReviewButton.visibility = userButtonsVisibility
            editReviewButton.visibility = userButtonsVisibility
        }

        private fun setupDeleteReviewButton(reviewId: String) {
            deleteReviewButton.setOnClickListener {
                feedViewModel.deleteReview(reviewId)
            }
        }

        private fun setupEditReviewButton(reviewId: String, restaurantId: String) {
            editReviewButton.setOnClickListener {
                val action =
                    FeedDirections.actionFeedFragmentToAddNewReviewFragment(
                        restaurantId,
                        reviewId
                    )
                itemView.findNavController().navigate(action)
            }
        }

        private fun canShowUserButtons(userId: String): Boolean {
            return Firebase.auth.currentUser?.uid == userId
        }

        private fun setupPopulatedReviewData(review: PopulatedReview) {
                    reviewTitle.text = review.title
                    reviewContent.text = review.content
                    timestamp.text = convertMillisToDateString(review.timestamp)
                    reviewUser.text = "${review.user.firstName ?: ""} ${review.user.lastName ?: ""}"
                    reviewRestaurant.text = "Restaurant: ${review.restaurant.name ?: ""}"

                    Glide.with(itemView.context)
                        .load(review.imageUri)
                        .into(reviewImage)
        }

        private fun setupReviewData(review: Review) {
                    reviewTitle.text = review.title
                    reviewContent.text = review.content
                    timestamp.text = convertMillisToDateString(review.timestamp)
                    reviewUser.text = review.userId
                    reviewRestaurant.text = "${review.restaurantId}"

                    Glide.with(itemView.context)
                        .load(review.remoteImageUri)
                        .into(reviewImage)
        }
    }
}