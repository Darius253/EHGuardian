package com.tron.ehguardian.ui.screens.authenticationScreens.signUp

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tron.ehguardian.data.models.UserModel
import com.tron.ehguardian.data.repositories.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SignUpViewModel(
    private val userRepository: UserRepository
) : ViewModel() {



    // Mutable live data for the input fields
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private var _isLoading = MutableLiveData(false) // Initially not loading
    var isLoading: LiveData<Boolean> = _isLoading

    private val _firstname = MutableLiveData<String>()
    val firstname: LiveData<String> = _firstname

    private val _surname = MutableLiveData<String>()
    val surname: LiveData<String> = _surname

    private val _gender = MutableLiveData<String>()
    val gender: LiveData<String> = _gender

    private val _weight = MutableLiveData<Double>()
    val weight: LiveData<Double> = _weight

    private val _height = MutableLiveData<Double>()
    val height: LiveData<Double> = _height

    private val _bloodSugarLevel = MutableLiveData<Double>()
    val bloodSugarLevel: LiveData<Double> = _bloodSugarLevel

    private val _cholesterolLevel = MutableLiveData<Double>()
    val cholesterolLevel: LiveData<Double> = _cholesterolLevel

    // Changed dateOfBirth to String to only store day/month/year
    private val _dateOfBirth = MutableLiveData<String>()
    val dateOfBirth: LiveData<String> = _dateOfBirth

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _errorMessage = MutableLiveData<String?>()
    private val errorMessage: LiveData<String?> = _errorMessage


    // Date format for day, month, and year
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Update email, password, name, etc.
    fun onEmailChanged(newEmail: String) { _email.value = newEmail }
    fun onPasswordChanged(newPassword: String) { _password.value = newPassword }
    fun onNameChanged(newFirstName: String) { _firstname.value = newFirstName }
    fun onSurnameChanged(newSurname: String) { _surname.value = newSurname }
    fun onGenderChanged(newGender: String) { _gender.value = newGender }
    fun onWeightChanged(newWeight: Double) { _weight.value = newWeight }
    fun onHeightChanged(newHeight: Double) { _height.value = newHeight }
//    fun onBloodSugarLevelChanged(newBloodSugarLevel: Double) { _bloodSugarLevel.value = newBloodSugarLevel }
//    fun onCholesterolLevelChanged(newCholesterolLevel: Double) { _cholesterolLevel.value = newCholesterolLevel }

    // Update date of birth with day, month, and year only
    fun onDateOfBirthChanged(newDateOfBirth: Date) {
        _dateOfBirth.value = dateFormatter.format(newDateOfBirth)
    }

    // Sign-up function
    fun signUp(user: UserModel, context: Context, onSignUpSuccess: () -> Unit) {
        if (!validateInputs())
            return
        clearErrorMessage()

        viewModelScope.launch {
            _isLoading.value = true
            delay(1000)
            try {
                userRepository.userSignUp(user, onComplete = {
                    success, errorMessage ->
                    if (success) {
                        Toast.makeText(context, "Sign up successful", Toast.LENGTH_SHORT).show()
                        onSignUpSuccess()
                        clearInputFields()
                        _isLoading.value = false
                    } else {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        _isLoading.value = false
                    }
                })
                clearErrorMessage()

//                clearInputFields() // Optional: clear fields after successful sign-up
            } catch (e: Exception) {
                _errorMessage.value = e.message
                Toast.makeText(context, "$errorMessage", Toast.LENGTH_SHORT).show()
                _isLoading.value = false
            }
        }
    }

    // Input validation function
    private fun validateInputs(): Boolean {
        return !(_email.value.isNullOrEmpty() || _password.value.isNullOrEmpty() ||
                _firstname.value.isNullOrEmpty() ||
                _surname.value.isNullOrEmpty() ||
                _gender.value.isNullOrEmpty() ||
                _dateOfBirth.value.isNullOrEmpty())
    }

    // Clear error message
    private fun clearErrorMessage() { _errorMessage.value = null }

//    // Clear input fields after sign-up
    private fun clearInputFields() {
        _email.value = ""
        _password.value = ""
        _firstname.value = ""
        _surname.value = ""
        _gender.value = ""
        _weight.value = 0.0
        _height.value = 0.0
        _bloodSugarLevel.value = 0.0
        _cholesterolLevel.value = 0.0
        _dateOfBirth.value = ""
    }

    fun signOut(
        onSignOutSuccess: () -> Unit,
        context: Context
    ) {
        viewModelScope.launch {
            delay(100)
            try {
                val success = userRepository.signOut()

                val message = if (success) {
                    onSignOutSuccess()
                    "Signed out successfully"
                } else {
                    "Failed to sign out"
                }

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
            catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }

}

    fun deleteAccount(context: Context, onDeleteSuccess: () -> Unit){
        viewModelScope.launch {
            val success = userRepository.deleteAccount()
            if (success) {
                Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                onDeleteSuccess()
            } else {
                Toast.makeText(context, "Failed to delete account", Toast.LENGTH_SHORT).show()
            }

        }

    }

}
