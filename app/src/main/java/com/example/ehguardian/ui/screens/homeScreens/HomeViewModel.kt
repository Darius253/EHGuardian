package com.example.ehguardian.ui.screens.homeScreens.home

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ehguardian.data.models.UserModel
import com.example.ehguardian.data.repositories.UserRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val userRepository: UserRepository) : ViewModel() {


    private val _userDetails = MutableLiveData<UserModel?>()
    val userDetails: LiveData<UserModel?> = _userDetails

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        fetchUserDetails(

        )
    }

    private fun fetchUserDetails(

    ) {
        viewModelScope.launch {
            try {
                userRepository.getUser().collect { user ->
                    _userDetails.value = user
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
}
