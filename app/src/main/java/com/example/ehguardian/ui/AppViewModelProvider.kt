package com.example.ehguardian.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ehguardian.EhGuardianApplication
import com.example.ehguardian.ui.screens.authenticationScreens.login.LoginViewModel
import com.example.ehguardian.ui.screens.authenticationScreens.signUp.SignUpViewModel
import com.example.ehguardian.ui.screens.homeScreens.HomeViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Access the application instance and its dependencies
        initializer {
            val app = (this as CreationExtras).ehGuardianApplication()
            LoginViewModel(app.container.userRepository)
        }
        initializer {
            val app = (this as CreationExtras).ehGuardianApplication()
            SignUpViewModel(app.container.userRepository)
        }
        initializer {
            val app = (this as CreationExtras).ehGuardianApplication()
            HomeViewModel(app.container.userRepository)
        }
    }
}

fun CreationExtras.ehGuardianApplication(): EhGuardianApplication {
    return (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as? EhGuardianApplication)
        ?: throw IllegalStateException("Application is not an instance of EhGuardianApplication")
}
