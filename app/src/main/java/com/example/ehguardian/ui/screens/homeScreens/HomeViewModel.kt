package com.example.ehguardian.ui.screens.homeScreens


import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ehguardian.data.models.HospitalItem
import com.example.ehguardian.data.models.MeasurementData
import com.example.ehguardian.data.models.NewsItem
import com.example.ehguardian.data.models.UserModel
import com.example.ehguardian.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val userRepository: UserRepository) : ViewModel() {


    private val _hospitals = MutableLiveData<List<HospitalItem>>()
    val hospitals: LiveData<List<HospitalItem>> = _hospitals

    private val _userDetails = MutableStateFlow<UserModel?>(null)
    val userDetails: StateFlow<UserModel?> = _userDetails

    private val _userMeasurements = MutableStateFlow<List<MeasurementData>>(emptyList())
    val userMeasurements: StateFlow<List<MeasurementData>> = _userMeasurements



    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _newsLiveData = MutableLiveData<List<NewsItem>>()
    val newsLiveData: LiveData<List<NewsItem>> = _newsLiveData

    init {
        fetchUserDetails()
        fetchUserMeasurements() // Ensure measurements are fetched on initialization
    }

     fun fetchUserDetails() {
        viewModelScope.launch {
            try {
                userRepository.getUser().collect { userDetails ->
                    _userDetails.value = userDetails
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

     fun fetchUserMeasurements() {
        viewModelScope.launch {
            try {
                userRepository.getUserMeasurements().collect { measurements ->
                    _userMeasurements.value = measurements
                }

                Log.d("HomeViewModel", "Fetched ${userMeasurements.value.size} measurements")
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun updateUserDetails(user: UserModel, context: Context) {
        viewModelScope.launch {
            try {
                val success = userRepository.updateUserDetails(user)
                _userDetails.value = user
                if (success) {
                    Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to update details", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
                Toast.makeText(context, "Failed to update details", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun uploadUserMeasurement(context: Context, measurementData: MeasurementData) {
        viewModelScope.launch {
            try {
                val success = userRepository.uploadUserMeasurement(measurementData)
                if (success) {
                    Toast.makeText(context, "Measurement uploaded successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to upload measurement", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
                Log.e("HomeViewModel", "Error uploading measurement: ${e.message}")
                Toast.makeText(context, "Failed to upload measurement", Toast.LENGTH_SHORT).show()
            }
        }
    }



    fun fetchHealthNews(context: Context) {
        viewModelScope.launch {
            try {
                // Fetch the news from the repository
                val newsList = userRepository.fetchHealthNews()

                // Update UI or LiveData with the fetched news
                _newsLiveData.value = newsList // Assuming _newsLiveData is a MutableLiveData<List<NewsItem>>

            } catch (e: Exception) {
                // Log and show error message
                Log.e("NewsViewModel", "Failed to fetch health news: ${e.message}", e)
                _errorMessage.value = e.message
                Toast.makeText(context, "Failed to fetch health news", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun fetchNearbyHospitals(context: Context) {
        viewModelScope.launch {
            try {
                // Fetch the hospitals from the repository
                val hospitalsList = userRepository.fetchNearbyHospitals(context)
                _hospitals.value = hospitalsList
            } catch (e: Exception) {
                // Log and show error message
                Log.e("HospitalViewModel", "Failed to fetch nearby hospitals: ${e.message}", e)
                _errorMessage.value = e.message
                Toast.makeText(context, "Failed to fetch nearby hospitals", Toast.LENGTH_SHORT).show()

            }

        }
    }

}





