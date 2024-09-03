package com.example.ehguardian.ui.screens.homeScreens.healthDataScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ehguardian.R

@Composable
fun HistoryPage(modifier: Modifier) {

Column {


    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        item {
            HistoryCard(systolic = 120, diastolic = 80, pulse = 72, bmi = 24.5)
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            HistoryCard(systolic = 135, diastolic = 85, pulse = 75, bmi = 25.0)
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            HistoryCard(systolic = 145, diastolic = 95, pulse = 78, bmi = 26.2)
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            HistoryCard(systolic = 115, diastolic = 75, pulse = 70, bmi = 23.8)
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            HistoryCard(systolic = 130, diastolic = 82, pulse = 74, bmi = 24.0)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
}

@Composable
fun HistoryCard(systolic: Int, diastolic: Int, pulse: Int, bmi: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .border(
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            BloodPressureCard(systolic = systolic, diastolic = diastolic)
            Spacer(modifier = Modifier.width(15.dp))
            HistoryDetails(systolic = systolic, diastolic = diastolic, pulse = pulse, bmi = bmi)
        }

    }
}

@Composable
fun BloodPressureCard(systolic: Int, diastolic: Int) {
    // Determine the background color based on blood pressure levels
    val backgroundColor = when {
        systolic >= 140 || diastolic >= 90 -> Color(0xFFFF6F6F) //
        systolic in 120..139 || diastolic in 80..89 -> Color(0xFFFFD966) //
        else -> Color(0xFF5CC87C) // Normal BP - Green
    }

    Card(
        modifier = Modifier
            .size(width = 80.dp, height = 100.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$systolic",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$diastolic",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun HistoryDetails(systolic: Int, diastolic: Int, pulse: Int, bmi: Double) {
    // Determine the status based on blood pressure levels
    val status = when {
        systolic >= 140 || diastolic >= 90 -> "Hypertension"
        systolic in 120..139 || diastolic in 80..89 -> "Elevated"
        else -> "Normal"
    }



    Column(
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "23-08-00, 12:00")
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(35.dp),
                painter = painterResource(id = R.drawable.hypertension),
                contentDescription = "Hypertension Indicator Image",

                )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = status,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Pulse: $pulse BPM",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.width(5.dp))
            VerticalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "BMI: $bmi kg/m²",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
