package com.example.ehguardian.ui.screens.homeScreens.dataScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ehguardian.R
import com.example.ehguardian.ui.screens.authenticationScreens.ToggleScreenButton

@Composable
fun DataHistoryScreen(modifier: Modifier) {
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
            HistoryScreen(modifier = modifier)
        }
        else {
            ChartScreen()
        }

    }

}