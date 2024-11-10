package com.tron.ehguardian.ui.screens.homeScreens.healthDataScreen


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
import com.tron.ehguardian.R
import com.tron.ehguardian.data.models.MeasurementData
@Composable
fun HistoryPage(
    modifier: Modifier,
    userMeasurements: List<MeasurementData> = emptyList(),
) {


    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(userMeasurements.size) { measurement ->
            HistoryCard(
                userMeasurements[measurement]
            )
        }
    }
}


@Composable
fun HistoryCard(measurementData: MeasurementData) {
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
            defaultElevation = 5.dp,
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            BloodPressureCard(systolic = measurementData.systolic, diastolic = measurementData.diastolic)
            Spacer(modifier = Modifier.width(15.dp))
            HistoryDetails(systolic = measurementData.systolic, diastolic = measurementData.diastolic,
                pulse = measurementData.pulse, bmi = measurementData.bmi, timestamp = measurementData.timestamp)
        }

    }
}

@Composable
fun BloodPressureCard(systolic: String, diastolic: String) {
    // Determine the background color based on blood pressure levels
    val backgroundColor = when {
        systolic.isBlank() || diastolic.isBlank() -> Color.Gray // Handle empty or null values
        (systolic.toIntOrNull() ?: 0) >= 140 || (diastolic.toIntOrNull() ?: 0) >= 90 -> Color(0xFFFF6F6F) // High BP - Red
        systolic.toIntOrNull() in 120..139 || diastolic.toIntOrNull() in 80..89 -> Color(0xFFFFD966) // Prehypertension - Yellow
        else -> Color(0xFF5CC87C) // Normal BP - Green
    }


    Card(
        modifier = Modifier
            .height(100.dp) ,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier.padding(start = 10.dp, top = 16.dp, end = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = systolic,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = diastolic,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun HistoryDetails(systolic: String, diastolic: String, pulse: String, bmi: String, timestamp: String) {
    // Determine the status based on blood pressure levels
    val status = when {
        systolic.isBlank() || diastolic.isBlank() -> "Invalid Data" // Handle empty or null values
        (systolic.toIntOrNull() ?: 0) >= 140 || (diastolic.toIntOrNull() ?: 0) >= 90 -> "Hypertension"
        systolic.toIntOrNull() in 120..139 || diastolic.toIntOrNull() in 80..89 -> "Elevated"
        else -> "Normal"
    }



    Column(
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = timestamp)
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
