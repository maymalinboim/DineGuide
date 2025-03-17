package com.example.myapplication.ui.myreviews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemMyReviewBinding
import com.example.myapplication.models.Review

class MyReviewsAdapter(private val viewModel: MyReviewsViewModel) : RecyclerView.Adapter<MyReviewsAdapter.MyReviewsViewHolder>() {

    private var reviews: List<Review> = listOf()

    fun setReviews(reviews: List<Review>) {
        this.reviews = reviews
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyReviewsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMyReviewBinding.inflate(layoutInflater, parent, false)
        return MyReviewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyReviewsViewHolder, position: Int) {
        holder.bind(reviews[position], viewModel)
    }

    override fun getItemCount(): Int = reviews.size

    class MyReviewsViewHolder(private val binding: ItemMyReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review, viewModel: MyReviewsViewModel) {
            binding.review = review
            binding.executePendingBindings()

            binding.editReviewButton.setOnClickListener {
                viewModel.onEditReviewClicked(review)
            }

            binding.deleteReviewButton.setOnClickListener {
                viewModel.onDeleteReviewClicked(review)
            }
        }
    }
}
