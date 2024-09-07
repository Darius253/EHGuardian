package com.example.ehguardian.ui.screens.authenticationScreens.signUp

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ehguardian.data.models.UserModel
import com.example.ehguardian.data.repositories.UserRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SignUpViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _id = UUID.randomUUID().toString()
    val id: String = _id


    // Mutable live data for the input fields
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _firstname = MutableLiveData<String>()
    val firstname: LiveData<String> = _firstname

    private val _surname = MutableLiveData<String>()
    val surname: LiveData<String> = _surname

    private val _gender = MutableLiveData<String>()
    val gender: LiveData<String> = _gender

    private val _weight = MutableLiveData<String>()
    val weight: LiveData<String> = _weight

    private val _height = MutableLiveData<String>()
    val height: LiveData<String> = _height

    // Changed dateOfBirth to String to only store day/month/year
    private val _dateOfBirth = MutableLiveData<String>()
    val dateOfBirth: LiveData<String> = _dateOfBirth

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Date format for day, month, and year
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Update email, password, name, etc.
    fun onEmailChanged(newEmail: String) { _email.value = newEmail }
    fun onPasswordChanged(newPassword: String) { _password.value = newPassword }
    fun onNameChanged(newFirstName: String) { _firstname.value = newFirstName }
    fun onSurnameChanged(newSurname: String) { _surname.value = newSurname }
    fun onGenderChanged(newGender: String) { _gender.value = newGender }
    fun onWeightChanged(newWeight: String) { _weight.value = newWeight }
    fun onHeightChanged(newHeight: String) { _height.value = newHeight }

    // Update date of birth with day, month, and year only
    fun onDateOfBirthChanged(newDateOfBirth: Date) {
        _dateOfBirth.value = dateFormatter.format(newDateOfBirth)
    }

    // Sign-up function
    fun signUp(user: UserModel, context: Context, onSignUpSuccess: () -> Unit) {
        if (!validateInputs()) return
        _isLoading.value = true
        clearErrorMessage()

        viewModelScope.launch {
            try {
                userRepository.userSignUp(user, onComplete = {
                    success, errorMessage ->
                    if (success) {
                        Toast.makeText(context, "Sign up successful", Toast.LENGTH_SHORT).show()
                        onSignUpSuccess()
                    } else {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                })
                clearErrorMessage()

                _isLoading.value = false
                clearInputFields() // Optional: clear fields after successful sign-up
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isLoading.value = false
            }
        }
    }

    // Input validation function
    private fun validateInputs(): Boolean {
        if (_email.value.isNullOrEmpty() || _password.value.isNullOrEmpty() ||
            _firstname.value.isNullOrEmpty() ||
            _surname.value.isNullOrEmpty() ||
            _gender.value.isNullOrEmpty() ||
            _dateOfBirth.value.isNullOrEmpty() ) {
            _errorMessage.value = "This field cannot be empty"
            return false
        }
        return true
    }

    // Clear error message
    private fun clearErrorMessage() { _errorMessage.value = null }

    // Clear input fields after sign-up
    private fun clearInputFields() {
        _email.value = ""
        _password.value = ""
        _firstname.value = ""
        _surname.value = ""
        _gender.value = ""
        _weight.value = ""
        _height.value = ""
        _dateOfBirth.value = ""
    }

    fun signOut(
        onSignOutSuccess: () -> Unit
    ){
        viewModelScope.launch {
            userRepository.signOut()
        }
        onSignOutSuccess()

    }
}
