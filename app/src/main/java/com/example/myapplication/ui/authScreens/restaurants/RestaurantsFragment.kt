package com.example.myapplication.ui.authScreens.restaurants

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.dal.repositories.RestaurantRepository
import com.example.myapplication.databinding.FragmentRestaurantsBinding
import com.example.myapplication.ui.components.RestaurantCardAdapter

class RestaurantsFragment : Fragment() {
    private lateinit var viewModel: RestaurantsViewModel
    private lateinit var restaurantRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentRestaurantsBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_restaurants, container, false
        )
        viewModel = RestaurantsViewModel(RestaurantRepository(requireContext()))
        bindViews(binding)
        setupRecyclerView(binding)
        setupLoading(binding)

        return binding.root
    }

    private fun bindViews(binding: FragmentRestaurantsBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setupRecyclerView(binding: FragmentRestaurantsBinding) {
        restaurantRecyclerView = binding.root.findViewById(R.id.restaurantRecyclerView)
        val layoutManager = LinearLayoutManager(context)
        restaurantRecyclerView.layoutManager = layoutManager
        val restaurantAdapter = RestaurantCardAdapter(emptyList())
        restaurantRecyclerView.adapter = restaurantAdapter

        viewModel.restaurants.observe(viewLifecycleOwner) { restaurants ->
            restaurantAdapter.updateRestaurants(restaurants)
        }
    }

    private fun setupLoading(binding: FragmentRestaurantsBinding) {
        progressBar = binding.root.findViewById(R.id.progress_bar)
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) showProgressBar()
            else hideProgressBar()
        }
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }
}