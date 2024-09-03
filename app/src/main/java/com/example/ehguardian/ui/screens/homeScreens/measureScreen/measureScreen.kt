package com.example.ehguardian.ui.screens.homeScreens.measureScreen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.ehguardian.R
import com.example.ehguardian.ui.screens.homeScreens.profile.CholesterolAndBloodSugarInputFields
import com.example.ehguardian.ui.screens.homeScreens.profile.InputField
import com.example.ehguardian.ui.screens.homeScreens.profile.WeightAndHeightInputFields

@Composable
fun MeasureScreen(modifier: Modifier = Modifier) {

    var showDialog by rememberSaveable { mutableStateOf(false) }
    var weight by rememberSaveable { mutableStateOf("") }
    var height by rememberSaveable { mutableStateOf("") }
    var cholesterolLevel by rememberSaveable { mutableStateOf("") }
    var bloodSugarLevel by rememberSaveable { mutableStateOf("") }
    var systolic by rememberSaveable { mutableStateOf("") }
    var diastolic by rememberSaveable { mutableStateOf("") }
    var heartRate by rememberSaveable { mutableStateOf("") }

    // Lottie composition
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.bluetooth))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    Box(
        modifier = modifier
            .fillMaxSize(),

        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = progress,
            modifier = Modifier.size(200.dp).padding(16.dp),
        )

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Details Manually")
        }
        if (showDialog) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), )
        }

        if (showDialog) {
            ManuallyAddDetails(
                onDismiss = { showDialog = false },
                weight = weight,
                height = height,
                cholesterolLevel = cholesterolLevel,
                bloodSugarLevel = bloodSugarLevel,
                systolic = systolic,
                diastolic = diastolic,
                heartRate = heartRate,
                onWeightChange = { weight = it },
                onHeightChange = { height = it },
                onCholesterolChange = { cholesterolLevel = it },
                onBloodSugarChange = { bloodSugarLevel = it },
                onSystolicChange = { systolic = it },
                onDiastolicChange = { diastolic = it },
                onHeartRateChange = { heartRate = it }
            )
        }
    }
}

@Composable
fun ManuallyAddDetails(

    onDismiss: () -> Unit,
    weight: String,
    height: String,
    cholesterolLevel: String,
    bloodSugarLevel: String,
    systolic: String,
    diastolic: String,
    heartRate: String,
    onWeightChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onCholesterolChange: (String) -> Unit,
    onBloodSugarChange: (String) -> Unit,
    onSystolicChange: (String) -> Unit,
    onDiastolicChange: (String) -> Unit,
    onHeartRateChange: (String) -> Unit
) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.95f)
                .padding(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            )

        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center

            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Manually Input Data",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Red
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Blood Pressure Input
                Row {
                    InputField(
                        label = "Systolic",
                        value = systolic,
                        onValueChange = onSystolicChange,
                        suffix = "mmHg",
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    InputField(
                        label = "Diastolic",
                        value = diastolic,
                        onValueChange = onDiastolicChange,
                        suffix = "mmHg",
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                // Heart Rate Input
                InputField(
                    label = "Heart Rate",
                    value = heartRate,
                    onValueChange = onHeartRateChange,
                    suffix = "bpm",
                    modifier = Modifier.weight(1f),
                    keyboardType = KeyboardType.Number
                )


                // Weight and Height Input
                WeightAndHeightInputFields(
                    weight = weight,
                    height = height,
                    onWeightChange = onWeightChange,
                    onHeightChange = onHeightChange
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Cholesterol and Blood Sugar Input
                CholesterolAndBloodSugarInputFields(
                    cholesterolLevel = cholesterolLevel,
                    bloodSugarLevel = bloodSugarLevel,
                    onCholesterolChange = onCholesterolChange,
                    onBloodSugarChange = onBloodSugarChange
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(
                    modifier = Modifier.align(Alignment.End),
                    onClick = { /*TODO*/ }) {
                    Text(
                        text = "Upload",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,

                    )

                }
            }
        }

}
