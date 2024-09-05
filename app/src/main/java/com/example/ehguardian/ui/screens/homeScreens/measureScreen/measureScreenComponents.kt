package com.example.ehguardian.ui.screens.homeScreens.measureScreen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.example.ehguardian.R
import com.example.ehguardian.ui.screens.homeScreens.profile.InputField


@Composable
fun BluetoothAnimation(composition: LottieComposition?, progress: Float) {
    LottieAnimation(
        composition = composition,
        progress = progress,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun AddDetailsFab(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.padding(top = 500.dp, start = 300.dp)
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Details Manually")
    }
}

@Composable
fun OverlayBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
    )
}

@Composable
fun ManuallyAddDetails(
    onDismiss: () -> Unit,
    systolic: String,
    diastolic: String,
    heartRate: String,
    onSystolicChange: (String) -> Unit,
    onDiastolicChange: (String) -> Unit,
    onHeartRateChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
            .padding(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            DialogHeader(onDismiss = onDismiss)
            Spacer(modifier = Modifier.height(16.dp))
            ExpandableSection(
                title = "Blood Pressure",
                iconResId = R.drawable.arm
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                BloodPressureInputs(
                    systolic = systolic,
                    diastolic = diastolic,
                    onSystolicChange = onSystolicChange,
                    onDiastolicChange = onDiastolicChange
                )
            }
            Spacer(modifier = Modifier.height(25.dp))
            ExpandableSection(
                title = "Heart Rate",
                iconResId = R.drawable.heart_rate_monitor
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                HeartRateInput(
                    heartRate = heartRate,
                    onHeartRateChange = onHeartRateChange
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            UploadButton(
                onUpload = { onDismiss() },
                systolic = systolic,
                diastolic = diastolic,
                heartRate = heartRate
            )
        }
    }
}

@Composable
fun DialogHeader(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Manually Input Data",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
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
}

@Composable
fun ExpandableSection(
    title: String,
    iconResId: Int,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(48.dp),
                painter = painterResource(id = iconResId),
                contentDescription = "$title Image"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        content()
    }
}

@Composable
fun BloodPressureInputs(
    systolic: String,
    diastolic: String,
    onSystolicChange: (String) -> Unit,
    onDiastolicChange: (String) -> Unit
) {
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
}

@Composable
fun HeartRateInput(
    heartRate: String,
    onHeartRateChange: (String) -> Unit
) {
    InputField(
        label = "Pulse",
        value = heartRate,
        onValueChange = onHeartRateChange,
        suffix = "bpm",
        modifier = Modifier.fillMaxWidth(),
        keyboardType = KeyboardType.Number
    )
}

@Composable
fun UploadButton(
    onUpload: () -> Unit,
    systolic: String,
    diastolic: String,
    heartRate: String
) {
    val context = LocalContext.current

    TextButton(
        modifier = Modifier.padding(start = 200.dp),
        onClick = {
            onUpload()
            Toast.makeText(context, "$systolic $diastolic $heartRate", Toast.LENGTH_SHORT).show()
        }
    ) {
        Text(
            text = "Upload",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}