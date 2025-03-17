package com.example.myapplication.ui.myreviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentMyReviewsBinding
import com.example.myapplication.models.Review
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MyReviewsFragment : Fragment() {

    private val viewModel: MyReviewsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentMyReviewsBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_my_reviews, container, false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupRecyclerView(binding)

        viewModel.editReviewEvent.observe(viewLifecycleOwner) { review ->
            review?.let {
                // Handle edit review event
                showEditReviewDialog(review)
                viewModel.onEditReviewEventHandled()
            }
        }

        viewModel.deleteReviewEvent.observe(viewLifecycleOwner) { review ->
            review?.let {
                // Handle delete review event
                showDeleteReviewDialog(review)
                viewModel.onDeleteReviewEventHandled()
            }
        }

        return binding.root
    }

    private fun setupRecyclerView(binding: FragmentMyReviewsBinding) {
        val recyclerView = binding.myReviewsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = MyReviewsAdapter(viewModel)
    }

    private fun showEditReviewDialog(review: Review) {
        // Show edit dialog
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Review")
            .setMessage("Edit the review for: ${review.title}")
            .setPositiveButton("Edit") { dialog, _ ->
                // Handle edit action
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showDeleteReviewDialog(review: Review) {
        // Show delete dialog
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Review")
            .setMessage("Are you sure you want to delete the review for: ${review.title}?")
            .setPositiveButton("Delete") { dialog, _ ->
                // Handle delete action
                viewModel.deleteReview(review)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
