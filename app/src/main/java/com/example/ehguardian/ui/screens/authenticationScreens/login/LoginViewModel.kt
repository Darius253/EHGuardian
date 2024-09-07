package com.example.ehguardian.ui.screens.authenticationScreens.login

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ehguardian.data.repositories.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository // Inject the repository
) : ViewModel() {

    // State for email and password
    var email = mutableStateOf("")
        private set

    var password = mutableStateOf("")
        private set

    var isLoading = mutableStateOf(false)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    // Update email and password
    fun onEmailChange(newEmail: String) {
        email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password.value = newPassword
    }


    fun signIn(
        context: Context,
        onSignInSuccess: () -> Unit) {
        isLoading.value = true
        if (email.value.isEmpty() || password.value.isEmpty()) {
            errorMessage.value = "Email and Password cannot be empty"
            Toast.makeText(context, errorMessage.value, Toast.LENGTH_SHORT).show()

            isLoading.value = false
            return
        }

        else {

        errorMessage.value = null

        viewModelScope.launch {
            try {
                userRepository.userSignIn(email.value, password.value,
                    onComplete = {
                        success, userId ->
                        if (success) {
                            isLoading.value = false
                            errorMessage.value = null
                            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                            onSignInSuccess()

                        }
                        else {
                            isLoading.value = false
                            errorMessage.value = "Login failed"
                            Toast.makeText(context, userId, Toast.LENGTH_SHORT).show()

                        }

                })


            } catch (e: Exception) {
                errorMessage.value = "Login failed: ${e.message}"
                Toast.makeText(context, errorMessage.value, Toast.LENGTH_SHORT).show()
                isLoading.value = false
            } finally {
                isLoading.value = false
            }
        }
    }
        }
}
