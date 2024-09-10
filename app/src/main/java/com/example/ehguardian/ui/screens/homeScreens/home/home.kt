package com.example.ehguardian.ui.screens.homeScreens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ehguardian.R
import com.example.ehguardian.ui.AppViewModelProvider
import com.example.ehguardian.ui.screens.homeScreens.HomeViewModel
import com.example.ehguardian.ui.screens.homeScreens.SettingsPopUp
import java.time.LocalTime

@SuppressLint("DefaultLocale")
@Composable
fun Home(modifier: Modifier = Modifier, onSignOut: () -> Unit,
         homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var settingsPopupVisible by remember { mutableStateOf(false) }
    val userDetails by homeViewModel.userDetails.collectAsState()

    val latestMeasurement by homeViewModel.userMeasurements.collectAsState()
   val newsList by homeViewModel.newsLiveData.observeAsState(emptyList())
    var showHealthTrends by rememberSaveable { mutableStateOf(false) }


    Box {
        if (latestMeasurement.isNotEmpty()) {
            LazyColumn(
                modifier = modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .fillMaxSize()
            ) {
                item {
                    Header(
                        firstName = userDetails?.firstname ?: "",
                        lastName = userDetails?.lastname ?: "",
                        onClickSettings = { settingsPopupVisible = true }
                    )

                }
                item { Spacer(modifier = Modifier.height(10.dp)) }
                item { SectionTitle("Here is your last Measurement") }
                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    HealthCard(
                        title = "Blood Pressure",
                        imageRes = R.drawable.arm,
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        textColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextData(
                                text = "Systolic",
                                value = latestMeasurement.first().systolic,
                                MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            TextData(
                              text=  "Diastolic",
                                value= latestMeasurement.first().diastolic,
                                MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            TextData("Pulse", "80", MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    HealthCard(
                        title = "Heart Rate",
                        imageRes = R.drawable.blood_pressure,
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        textColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        TextData(
                            text = "bpm",
                            value = latestMeasurement.first().pulse,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(start = 60.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        item {
                            HealthCard(
                                title = "Body Mass Index",
                                imageRes = R.drawable.heart_rate_monitor,
                                backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                                textColor = MaterialTheme.colorScheme.onTertiaryContainer
                            ) {
                                TextData(
                                    text = "kg/m²",
                                    value = (
                                            if (userDetails != null) {
                                                val weight = userDetails!!.userWeight.toDouble()
                                                val height = userDetails!!.userHeight.toDouble()

                                                if (weight > 0 && height > 0) {
                                                    // Perform the BMI calculation with floating-point division
                                                    val bmi = weight / (height * height)

                                                    // Format the result to a string with two decimal places
                                                    String.format("%.2f", bmi)
                                                } else {
                                                    "N/A" // Handle invalid weight or height (e.g. 0 or negative values)
                                                }
                                            } else {
                                                "N/A"
                                            }
                                            ).toString(),
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(start = 60.dp)
                                )

                            }
                        }
                        item {
                            HealthCard(
                                title = "Cholesterol",
                                imageRes = R.drawable.cholesterol,
                                backgroundColor = Color(0xFFDDC842),
                                textColor = Color.White
                            ) {
                                TextData(
                                    text = "mg/dL",
                                    value = (
                                            if (userDetails != null) {
                                                userDetails!!.cholesterolLevel.ifEmpty {
                                                    "N/A"
                                                }

                                            } else {
                                                "N/A"
                                            }
                                            ),
                                    color = Color.White,
                                    modifier = Modifier.padding(start = 60.dp)
                                )
                            }
                        }
                        item {
                            HealthCard(
                                title = "Blood Sugar",
                                imageRes = R.drawable.sugar_blood_level,
                                backgroundColor = Color(0xFF5CC87C),
                                textColor = Color.White
                            ) {
                                TextData(
                                    text = "mg/dL",
                                    value = if (userDetails != null) {
                                        userDetails!!.bloodSugarLevel.ifEmpty {
                                            "N/A"
                                        }

                                    } else {
                                        "N/A"
                                    },
                                    color = Color.White,
                                    modifier = Modifier.padding(start = 60.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item { LatestHealthTrends(
                    onReadMoreClick = {
                            showHealthTrends = true

                    }
                ) }
            }
            if (showHealthTrends) {
                HealthTrends(
                    onDismiss = { showHealthTrends = false },
                            newsList
                )
            }
            if (settingsPopupVisible) {
                SettingsPopUp(

                    onDismiss = { settingsPopupVisible = false },
                    onSignOutSuccess = onSignOut

                )
            }

        }
        else {
            Column(
                modifier = modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .fillMaxSize()
            ) {
                Header(
                    firstName = userDetails?.firstname ?: "",
                    lastName = userDetails?.lastname ?: "",
                    onClickSettings = { settingsPopupVisible = true }
                )
                Column (
                        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                        ){

                    Image(
                        painter = painterResource(id = R.drawable.clipboard),
                        contentDescription = "No data Image"
                    )
                    Text(
                        text = "No data available",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
              LatestHealthTrends(
                  onReadMoreClick = {
                      showHealthTrends = true
                  }

              )
                if (showHealthTrends) {
                    HealthTrends(
                        onDismiss = { showHealthTrends = false },
                                newsList
                    )
                }
                if (settingsPopupVisible) {
                    SettingsPopUp(

                        onDismiss = { settingsPopupVisible = false },
                        onSignOutSuccess = onSignOut

                    )
                }
            }
        }
    }

}

@Composable
fun Header(
    firstName: String,
    lastName: String,
    modifier: Modifier = Modifier,
           onClickSettings: () -> Unit = {}) {
    val currentHour = remember { LocalTime.now().hour }
    val greeting = when (currentHour) {
        in 5..11 -> "Good Morning,"
        in 12..17 -> "Good Afternoon,"
        in 18..21 -> "Good Evening,"
        else -> "Good Night,"
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(greeting, style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "$firstName $lastName",
                style = MaterialTheme.typography.headlineSmall,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }

        IconButton(onClick = onClickSettings) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                modifier = Modifier.size(32.dp)
            )
        }
    }

}

@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}

@Composable
fun HealthCard(
    title: String,
    imageRes: Int,
    backgroundColor: Color,
    textColor: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .height(200.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(25.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "$title Image",
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun TextData(
    text: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Normal,
            color = color
        )
    }
}

@Composable
fun LatestHealthTrends(
    modifier: Modifier = Modifier,
    onReadMoreClick: () -> Unit,
    ) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        SectionTitle(
            title = "Latest Health Trends",
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onReadMoreClick) {
            Icon(
                imageVector = Icons.Outlined.Newspaper,
                contentDescription = "Read More",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
