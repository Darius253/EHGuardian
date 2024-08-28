package com.example.ehguardian.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ehguardian.ui.screens.authenticationScreens.AuthenticationScreen


@Composable
fun MyApp() {

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

        AuthenticationScreen(modifier = Modifier.padding(innerPadding))
    }
}