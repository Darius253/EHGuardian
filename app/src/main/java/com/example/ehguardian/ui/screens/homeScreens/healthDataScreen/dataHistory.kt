package com.example.ehguardian.ui.screens.homeScreens.healthDataScreen


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ehguardian.ui.screens.authenticationScreens.ToggleScreenButton

@Composable
fun HealthDataScreen(modifier: Modifier) {
    var isHistory by remember { mutableStateOf(true) }
    Column {
        ToggleScreenButton(
            isLogin = isHistory,
            onButtonClick = { isHistory = !isHistory },
            firstText = "History",
            secondText = "Chart",
            color = MaterialTheme.colorScheme.primaryContainer,
        )
        
        if (isHistory) {
            HistoryPage(modifier = modifier)
        }
        else {
            ChartPage(modifier = modifier)
        }

    }

}