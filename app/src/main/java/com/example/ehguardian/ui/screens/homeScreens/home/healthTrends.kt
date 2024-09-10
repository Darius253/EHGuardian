package com.example.ehguardian.ui.screens.homeScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@Composable
fun HealthTrends(){

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = "Latest Health Trends For Good Health",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp),
            )
        Row {
            Column {
                Text(
                    text = "Latest Health Trend HeadLine",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)

                )
                Text(text = "")

            }
        }
    }
}