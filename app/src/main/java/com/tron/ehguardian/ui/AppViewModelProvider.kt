package com.tron.ehguardian.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tron.ehguardian.EhGuardianApplication
import com.tron.ehguardian.ui.screens.authenticationScreens.login.LoginViewModel
import com.tron.ehguardian.ui.screens.authenticationScreens.signUp.SignUpViewModel
import com.tron.ehguardian.ui.screens.homeScreens.HomeViewModel
import com.tron.ehguardian.ui.screens.homeScreens.measureScreen.BluetoothViewModel

object AppViewModelProvider {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        // Access the application instance and its dependencies
        initializer {
            val app = (this).ehGuardianApplication()
            LoginViewModel(app.container.userRepository)
        }
        initializer {
            val app = (this).ehGuardianApplication()
            SignUpViewModel(app.container.userRepository)
        }
        initializer {
            val app = (this).ehGuardianApplication()
            HomeViewModel(app.container.userRepository)
        }

        initializer {
//            val app = (this).ehGuardianApplication()
            BluetoothViewModel()
        }
    }
}

fun CreationExtras.ehGuardianApplication(): EhGuardianApplication {
    return (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as? EhGuardianApplication)
        ?: throw IllegalStateException("Application is not an instance of EhGuardianApplication")
}
