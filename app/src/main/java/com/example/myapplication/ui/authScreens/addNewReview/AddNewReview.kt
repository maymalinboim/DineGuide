package com.example.myapplication.ui.authScreens.addNewReview

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.R
import com.example.myapplication.dal.repositories.RestaurantRepository
import com.example.myapplication.dal.repositories.ReviewsRepository
import com.example.myapplication.databinding.FragmentAddNewReviewBinding
import com.example.myapplication.ui.views.ImagePicker
import com.example.myapplication.utils.BasicAlert
import com.yalantis.ucrop.UCrop

class AddNewReview : Fragment() {

    companion object {
        fun newInstance() = AddNewReview()
    }

    private val args: AddNewReviewArgs by navArgs()
    private lateinit var viewModel: AddNewReviewViewModel
    lateinit var imagePickerView: ImagePicker
    lateinit var progressBar: ProgressBar
    private lateinit var saveChangesButton: Button
    private val imagePicker: ActivityResultLauncher<String> = getImagePicker()
    private val uCropLauncher: ActivityResultLauncher<Intent> = getUCropLauncher()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentAddNewReviewBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_new_review, container, false
        )
        viewModel = AddNewReviewViewModel(
            ReviewsRepository(requireContext()),
            RestaurantRepository(requireContext())
        )
        bindViews(binding)
        setUpSaveChangesButton(binding)
        viewModel.fetchRestaurant(args.restaurantId)
        if (args.reviewId.isNotEmpty()) viewModel.fetchReview(args.reviewId)
        setupImagePicker(binding)
        setupLoading(binding)
        return binding.root
    }

    private fun bindViews(binding: FragmentAddNewReviewBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setUpSaveChangesButton(binding: FragmentAddNewReviewBinding) {
        saveChangesButton = binding.root.findViewById(R.id.save_changes_button)
        saveChangesButton.setOnClickListener {
            viewModel.saveReview({
                BasicAlert("Success", "Review saved successfully", requireContext()).show()
                findNavController().navigate(R.id.action_addNewReviewFragment_to_feedFragment)
            }, {
                BasicAlert("Fail", "Failed to save review", requireContext()).show()
            })
        }
    }

    private fun setupImagePicker(binding: FragmentAddNewReviewBinding) {
        imagePickerView = binding.root.findViewById(R.id.image_view)
        imagePickerView.setOnClickListener {
            imagePicker.launch("image/*")
        }
        viewModel.imageUri.observe(viewLifecycleOwner) { uri ->
            if (uri != "") imagePickerView.setImageURI(uri.toUri())
        }
    }

    private fun getImagePicker() =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imagePickerView.getImagePicker(uri, uCropLauncher)
        }

    private fun getUCropLauncher() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = UCrop.getOutput(result.data!!)
                viewModel.imageUri.value = uri.toString()
            }
        }

    private fun setupLoading(binding: FragmentAddNewReviewBinding) {
        progressBar = binding.root.findViewById(R.id.progress_bar)
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) showProgressBar()
            else showSaveChangesButton()
        }
    }

    private fun showSaveChangesButton() {
        saveChangesButton.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        saveChangesButton.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }
}