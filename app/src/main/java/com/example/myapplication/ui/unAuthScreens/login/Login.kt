package com.example.myapplication.ui.unAuthScreens.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentLoginBinding
import com.example.myapplication.utils.BasicAlert
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class Login : Fragment() {

    companion object {
        fun newInstance() = Login()
    }

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var registerLink: View
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentLoginBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login, container, false
        )
        bindViews(binding)
        if (FirebaseAuth.getInstance().currentUser != null) {
            findNavController().navigate(R.id.action_loginFragment_to_feedFragment)
        }
        setupRegisterLink(binding)
        setupLoginButton(binding)

        return binding.root
    }

    private fun bindViews(binding: FragmentLoginBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setupRegisterLink(binding: FragmentLoginBinding) {
        registerLink = binding.root.findViewById(R.id.register_link)
        registerLink.setOnClickListener {
            findNavController().navigate(R.id.login_to_register)
        }
    }

    private fun setupLoginButton(binding: FragmentLoginBinding) {
        loginButton = binding.root.findViewById(R.id.login_button)
        progressBar = binding.root.findViewById(R.id.progress_bar)


        loginButton.setOnClickListener {
            showProgressBar()
            viewModel.login({ onLoginSuccess() }, { error -> onLoginFailure(error) })
        }
    }

    private fun onLoginSuccess() {
        BasicAlert("Login", "Login Successful", requireContext()).show()
    }

    private fun onLoginFailure(error: Exception?) {
        if (error == null) {
            showLoginButton()
            return
        }

        Log.e("Login", "Error signing in user", error)
        handleLoginError(error)
        showLoginButton()
    }

    private fun handleLoginError(error: Exception) {
        when (error) {
            is FirebaseAuthInvalidUserException, is FirebaseAuthInvalidCredentialsException -> {
                BasicAlert("Login Error", "User not found", requireContext()).show()
            }

            else -> {
                BasicAlert("Login Error", "An error occurred", requireContext()).show()
            }
        }
    }

    private fun showLoginButton() {
        loginButton.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        loginButton.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }
}