package com.example.ehguardian.ui.screens.authenticationScreens.login

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ehguardian.data.repositories.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository // Inject the repository
) : ViewModel() {

    // State for email and password
    var email = mutableStateOf("")
        private set

    var password = mutableStateOf("")
        private set

    private var _isLoading = MutableLiveData(false) // Initially not loading
    var isLoading: LiveData<Boolean> = _isLoading


    private var errorMessage = mutableStateOf<String?>(null)


    // Update email and password
    fun onEmailChange(newEmail: String) {
        email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password.value = newPassword
    }


    fun signIn(
        context: Context,
        onSignInSuccess: () -> Unit
    ) {

        if (email.value.isEmpty() || password.value.isEmpty()) {
            errorMessage.value = "Email and Password cannot be empty"
            Toast.makeText(context, errorMessage.value, Toast.LENGTH_SHORT).show()
            return
        } else {
            errorMessage.value = null
            _isLoading.value = true
            // Show loading indicator
            viewModelScope.launch {
                try {
                    delay(
                        1000
                    )
                    userRepository.userSignIn(email.value, password.value) { success, error ->
                        if (success) {
                            errorMessage.value = null
                            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                            onSignInSuccess()
                        } else {
                            errorMessage.value = "Login failed"
                            Toast.makeText(context, "Login failed: $error", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    errorMessage.value = "Login failed: ${e.message}"
                    Toast.makeText(context, errorMessage.value, Toast.LENGTH_LONG).show()
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

}
