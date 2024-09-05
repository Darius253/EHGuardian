package com.example.ehguardian.ui.screens.authenticationScreens.signUp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ehguardian.data.models.MeasurementData
import com.example.ehguardian.data.models.UserModel
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {

    // Mutable live data for the input fields
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _surname = MutableLiveData<String>()
    val surname: LiveData<String> = _surname

    private val _gender = MutableLiveData<String>()
    var gender: LiveData<String> = _gender

    private val _weight = MutableLiveData<String>()
    val weight: LiveData<String> = _weight

    private val _height = MutableLiveData<String>()
    val height: LiveData<String> = _height

    private val _dateOfBirth = MutableLiveData<String>()
    val dateOfBirth: LiveData<String> = _dateOfBirth


    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password



    private val _signUpSuccess = MutableLiveData<Boolean>()
    val signUpSuccess: LiveData<Boolean> = _signUpSuccess

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage


    // Method to create a UserModel
    private fun createUserModel(): UserModel {
        val emailValue = _email.value ?: ""
        val passwordValue = _password.value ?: ""
        val nameValue = _name.value ?: ""
        val surnameValue = _surname.value ?: ""
        val genderValue = _gender.value ?: ""
        val weightValue = _weight.value ?: ""
        val heightValue = _height.value ?: ""
        val dateOfBirthValue = _dateOfBirth.value ?: ""

        return UserModel(
            id = 0, // You might want to generate or fetch this from the server
            name = "$nameValue $surnameValue",
            email = emailValue,
            gender = genderValue,
            status = "active", // or any default value
            createdAt = System.currentTimeMillis().toString(),
            password = passwordValue,
            firstName = nameValue,
            lastName = surnameValue,
            userWeight = weightValue,
            userHeight = heightValue,
            dateOfBirth = dateOfBirthValue,
            measurementData = MeasurementData(
                0,
                "0",
                "0",
                "0",
                "0",
                0,


                ) // Replace with actual data if needed
        )
    }

    // Update email value
    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
    }

    // Update password value
    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
    }



    fun onNameChanged(newName: String) {
        _name.value = newName
    }

    fun onSurnameChanged(newSurname: String) {
        _surname.value = newSurname

    }

    fun onGenderChanged(newGender: String) {
        _gender.value = newGender
    }

    fun onWeightChanged(newWeight: String) {
        _weight.value = newWeight
    }

    fun onHeightChanged(newHeight: String) {
        _height.value = newHeight
    }

    fun onDateOfBirthChanged(newDateOfBirth: String) {
        _dateOfBirth.value = newDateOfBirth
    }






    // Sign-up function
    fun signUp() {
        val userModel = createUserModel()



        // Check if email or passwords are empty
        if (userModel.email.isEmpty()
            || userModel.password.isEmpty()
            || userModel.firstName.isEmpty()
            || userModel.lastName.isEmpty()
            || userModel.gender.isEmpty()
            || userModel.userWeight.isEmpty()
            || userModel.userHeight.isEmpty()
            ) {
            _errorMessage.value = "All fields are required"
            return
        }


        // If all validations pass, attempt to sign up (mocked here)
        viewModelScope.launch {
            // Mocking sign-up process; replace with actual authentication logic
            if (mockSignUp(userModel) ){
                _signUpSuccess.value = true
            } else {
                _errorMessage.value = "Sign up failed. Please try again."
            }
        }
    }

    // Reset error message
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    // Mock sign-up function; replace with real logic
    private suspend fun mockSignUp(
        userModel: UserModel,

    ): Boolean {
        // Simulating a network call with a delay
        kotlinx.coroutines.delay(2000)
        return userModel.email == "test@example.com" && userModel.password == "password"
    }
}
