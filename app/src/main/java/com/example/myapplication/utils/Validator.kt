package com.example.myapplication.utils

import android.util.Patterns

class Validator {
    private val _passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$".toRegex()
    fun validateEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePassword(password: String): Boolean {
        return _passwordPattern.matches(password)
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    fun validateName(name: String): Boolean {
        return name.isNotEmpty()
    }

    fun validateImageUri(imageUri: String): Boolean {
        return imageUri.isNotEmpty()
    }

    fun validateTitle(title: String): Boolean {
        return title.isNotEmpty()
    }

    fun validateContent(content: String): Boolean {
        return content.isNotEmpty()
    }
}