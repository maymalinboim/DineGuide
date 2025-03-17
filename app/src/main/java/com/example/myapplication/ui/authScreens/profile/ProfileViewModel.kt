package com.example.myapplication.ui.authScreens.profile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.dal.repositories.UserRepository
import com.example.myapplication.models.User
import com.example.myapplication.utils.Validator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val validator = Validator()

    val firstName = MutableLiveData("")
    val lastName = MutableLiveData("")
    val imageUri = MutableLiveData("")

    val isFirstNameValid = MutableLiveData(true)
    val isLastNameValid = MutableLiveData(true)
    val isImageUriValid = MutableLiveData(true)
    val isLoading = MutableLiveData(false)


    val isFormValid: Boolean
        get() = isFirstNameValid.value!! && isLastNameValid.value!! && isImageUriValid.value!!

    init {
        fetchUserDetails()
    }

    private fun fetchUserDetails() {
        isLoading.value = true
        try {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
                    val user = userRepository.getUserById(userId)
                    withContext(Dispatchers.Main) { setUserFields(user) }
                } catch (e: Exception) {
                    Log.e("Profile", "Error fetching user details", e)
                } finally {
                    withContext(Dispatchers.Main) { isLoading.value = false }
                }
            }
        } catch (e: Exception) {
            Log.e("Profile", "Error fetching user details", e)
        }
    }

    private fun setUserFields(user: User) {
        firstName.value = user.firstName!!
        lastName.value = user.lastName!!
        imageUri.value = user.imageUri!!
    }

    fun saveChanges(onSuccess: () -> Unit, onFailure: (error: Exception?) -> Unit) {
        validateForm()

        if (!isFormValid) {
            onFailure(null)
            return
        }

        try {
            updateUserConcurrently(onSuccess, onFailure)
        } catch (e: Exception) {
            Log.e("Profile", "Error updating user", e)
            onFailure(e)
        }
    }

    private fun updateUserConcurrently(
        onSuccess: () -> Unit, onFailure: (error: Exception?) -> Unit
    ) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = constructUserFromFields()
                userRepository.saveUserInDB(user)
                userRepository.saveUserImage(user.imageUri!!, user.id)
                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                Log.e("Profile", "Error updating user", e)
                withContext(Dispatchers.Main) { onFailure(e) }
            } finally {
                withContext(Dispatchers.Main) { isLoading.value = false }
            }
        }
    }

    private fun validateForm() {
        isFirstNameValid.value = validator.validateName(firstName.value!!)
        isLastNameValid.value = validator.validateName(lastName.value!!)
        isImageUriValid.value = validator.validateImageUri(imageUri.value!!)
    }

    private fun constructUserFromFields(): User {
        val user = User(
            firstName = firstName.value!!,
            lastName = lastName.value!!,
            email = auth.currentUser?.email!!,
            id = auth.currentUser!!.uid
        )
        user.imageUri =
            if (!imageUri.value!!.startsWith("file:///")) "file://${imageUri.value!!}"
            else imageUri.value!!
        return user
    }
}