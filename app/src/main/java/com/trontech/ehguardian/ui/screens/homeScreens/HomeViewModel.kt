package com.trontech.ehguardian.ui.screens.homeScreens



import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trontech.ehguardian.data.models.HospitalItem
import com.trontech.ehguardian.data.models.MeasurementData
import com.trontech.ehguardian.data.models.NewsItem
import com.trontech.ehguardian.data.models.UserModel
import com.trontech.ehguardian.data.repositories.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {





    private val _hospitals = MutableLiveData<List<HospitalItem>>()
    val hospitals: LiveData<List<HospitalItem>> = _hospitals

    private val _userDetails = MutableStateFlow<UserModel?>(null)
    val userDetails: StateFlow<UserModel?> = _userDetails

    private val _userMeasurements = MutableStateFlow<List<MeasurementData>>(emptyList())
    val userMeasurements: StateFlow<List<MeasurementData>> = _userMeasurements

  private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading






    private val _errorMessage = MutableLiveData<String?>()

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

            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun updateUserDetails(user: UserModel, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                delay(1000)
                val success = userRepository.updateUserDetails(user, context)
                _userDetails.value = user
                if (success) {
                    Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    _isLoading.value = false
                } else {
                    Toast.makeText(context, "Failed to update details", Toast.LENGTH_SHORT).show()
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
                Toast.makeText(context, "Failed to update details", Toast.LENGTH_LONG).show()
                _isLoading.value = false
            }
        }
    }

    fun uploadUserMeasurement(context: Context, measurementData: MeasurementData, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _isLoading.value = false
                val success = userRepository.uploadUserMeasurement(measurementData)
                if (success) {
                    Toast.makeText(context, "Measurement uploaded successfully", Toast.LENGTH_SHORT).show()
                    onSuccess()
                } else {
                    Toast.makeText(context, "Failed to upload measurement", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
                Toast.makeText(context, "Failed to upload measurement", Toast.LENGTH_SHORT).show()
            }
        }
    }



    fun fetchHealthNews(context: Context) {
        _isLoading.value = true
        viewModelScope.launch {
            delay(1000)
            try {
                // Fetch the news from the repository
                val newsList = userRepository.fetchHealthNews()

                // Update UI or LiveData with the fetched news
                _newsLiveData.value = newsList // Assuming _newsLiveData is a MutableLiveData<List<NewsItem>>
                _isLoading.value = false
            } catch (e: Exception) {
                // Log and show error message
                _errorMessage.value = e.message
                Toast.makeText(context, "Failed to fetch health news", Toast.LENGTH_SHORT).show()
                _isLoading.value = false
            }
        }
    }

    fun fetchNearbyHospitals(context: Context) {
        _isLoading.value = true
        viewModelScope.launch {
            delay(1000)
            try {
                // Fetch the hospitals from the repository
                val hospitalsList = userRepository.fetchNearbyHospitals(context)
                _hospitals.value = hospitalsList
                _isLoading.value = false
            } catch (e: Exception) {
                // show error message
                _errorMessage.value = e.message
                Toast.makeText(context, "Failed to fetch nearby hospitals", Toast.LENGTH_SHORT).show()
                _isLoading.value = false

            }



        }
    }










}





