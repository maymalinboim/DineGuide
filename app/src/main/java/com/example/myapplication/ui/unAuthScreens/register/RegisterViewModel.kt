package com.example.myapplication.ui.unAuthScreens.register

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.dal.repositories.UserRepository
import com.example.myapplication.models.User
import com.example.myapplication.utils.Validator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    val firstName = MutableLiveData("")
    val lastName = MutableLiveData("")
    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val confirmPassword = MutableLiveData("")
    val imageUri = MutableLiveData("")


    val isFirstNameValid = MutableLiveData(true)
    val isLastNameValid = MutableLiveData(true)
    val isEmailValid = MutableLiveData(true)
    val isPasswordValid = MutableLiveData(true)
    val isConfirmPasswordValid = MutableLiveData(true)
    val isImageUriValid = MutableLiveData(true)

    val isFormValid: Boolean
        get() = isFirstNameValid.value!! && isLastNameValid.value!! && isEmailValid.value!!
                && isPasswordValid.value!! && isConfirmPasswordValid.value!! && isImageUriValid.value!!

    private val validator = Validator()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    fun register(onSuccess: () -> Unit, onFailure: (error: Exception?) -> Unit) {
        validateForm()

        if (!isFormValid) {
            onFailure(null)
            return
        }

        try {
            createUserConcurrently(onSuccess, onFailure)
        } catch (e: Exception) {
            Log.e("Register", "Error registering user", e)
            onFailure(e)

        }
    }

    private fun createUserConcurrently(
        onSuccess: () -> Unit,
        onFailure: (error: Exception?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                createAuthUser(email.value!!, password.value!!)
                saveUser(constructUserFromFields())
                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                Log.e("Register", "Error registering user", e)
                withContext(Dispatchers.Main) { onFailure(e) }
            }
        }
    }

    private fun validateForm() {
        isFirstNameValid.value = validator.validateName(firstName.value!!)
        isLastNameValid.value = validator.validateName(lastName.value!!)
        isEmailValid.value = validator.validateEmail(email.value!!)
        isPasswordValid.value = validator.validatePassword(password.value!!)
        isConfirmPasswordValid.value =
            validator.validateConfirmPassword(password.value!!, confirmPassword.value!!)
        isImageUriValid.value = validator.validateImageUri(imageUri.value!!)
    }

    private fun constructUserFromFields(): User {
        val user = User(
            firstName = firstName.value!!,
            lastName = lastName.value!!,
            email = email.value!!,
            id = auth.currentUser!!.uid
        )
        user.imageUri = imageUri.value!!
        return user
    }

    private suspend fun createAuthUser(email: String, password: String) {
        val task = auth.createUserWithEmailAndPassword(email, password).await()
        if (task.user?.uid == null) throw Exception("User not created")
    }

    private suspend fun saveUser(user: User) {
        try {
            userRepository.saveUserInDB(user)
            userRepository.saveUserImage(user.imageUri!!, user.id)
        } catch (e: Exception) {
            Log.e("Register", "Error saving user", e)
            throw e
        }


        Log.i("UserRepository", "User ${user.json} saved in DB")
    }
}