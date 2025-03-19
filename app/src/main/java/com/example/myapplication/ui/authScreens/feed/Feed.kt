// File: java/com/example/myapplication/ui/authScreens/feed/FeedFragment.kt

package com.example.myapplication.ui.authScreens.feed

import FeedViewModelFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.dal.repositories.ImageRepository
import com.example.myapplication.dal.repositories.RestaurantRepository
import com.example.myapplication.dal.repositories.ReviewsRepository
import com.example.myapplication.dal.repositories.UserRepository
import com.example.myapplication.databinding.FragmentFeedBinding

class Feed : Fragment() {

    private val args: FeedArgs by navArgs()
    private lateinit var viewModel: FeedViewModel
    private lateinit var reviewRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentFeedBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_feed, container, false
        )

        val factory = FeedViewModelFactory(
            args.isMyFeed,
            ReviewsRepository(requireContext()),
            ImageRepository(requireContext()),
            UserRepository(requireContext()),
            RestaurantRepository(requireContext())
        )

        viewModel = ViewModelProvider(this, factory)[FeedViewModel::class.java]

        bindViews(binding)
        setupRecyclerView(binding)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchReviews()
    }

    private fun bindViews(binding: FragmentFeedBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setupRecyclerView(binding: FragmentFeedBinding) {
        reviewRecyclerView = binding.root.findViewById(R.id.reviewRecyclerView)
        val layoutManager = LinearLayoutManager(context)
        reviewRecyclerView.layoutManager = layoutManager
        val reviewAdapter = ReviewCardAdapter(emptyList(), viewModel)
        reviewRecyclerView.adapter = reviewAdapter

        viewModel.reviews.observe(viewLifecycleOwner) { reviews ->
            Log.d("FeedFragment", "Updating reviews in adapter: $reviews")
            reviewAdapter.updateReviews(reviews)
        }
    }
}