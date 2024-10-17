package com.example.ehguardian.ui.screens

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.ehguardian.ui.AppNavigation

@Composable
fun MyApp(

) {
    // Remembering the NavController for handling navigation
    val navController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        // Passing the NavController and modifier with padding to the AppNavigation
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
