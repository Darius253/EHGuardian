import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

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

    // Simulate login process
    fun login() {
        if (email.value.isEmpty() || password.value.isEmpty()) {
            errorMessage.value = "Email and Password cannot be empty"
            return
        }

        isLoading.value = true
        errorMessage.value = null

        // Simulating a network call
        viewModelScope.launch {
            try {
                // Simulate network delay
                delay(2000)

                Log.d("Login", "${email.value} ${password.value}")

                // For now, just simulate a successful login
                isLoading.value = false
                errorMessage.value = null // Clear error
            } catch (e: Exception) {
                isLoading.value = false
                errorMessage.value = "Login failed. Try again."
            }
        }
    }
}
