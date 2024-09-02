package com.example.ehguardian.ui.screens.homeScreens.home

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ehguardian.R
import com.example.ehguardian.ui.screens.homeScreens.SettingsPopUp
import java.time.LocalTime

@Composable
fun Home(modifier: Modifier = Modifier) {
    var settingsPopupVisible by remember { mutableStateOf(false) }
    Box {

        LazyColumn(
            modifier = modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxSize()
        ) {
            item{
                Header(
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
                        TextData("Systolic", "120", MaterialTheme.colorScheme.onPrimaryContainer)
                        TextData("Diastolic", "80", MaterialTheme.colorScheme.onPrimaryContainer)
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
                        value = "82",
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
                                value = "24",
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
                                value = "200",
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
                                value = "100",
                                color = Color.White,
                                modifier = Modifier.padding(start = 60.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item { LatestHealthTrends() }
        }
        if (settingsPopupVisible) {
            SettingsPopUp(

                onDismiss = { settingsPopupVisible = false }

            )
        }

    }
}

@Composable
fun Header(modifier: Modifier = Modifier,
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
                text = "Darius Twumasi-Ankrah",
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
fun LatestHealthTrends(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        SectionTitle(
            title = "Latest Health Trends",
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = { /* Handle settings icon click */ }) {
            Icon(
                imageVector = Icons.Outlined.Newspaper,
                contentDescription = "Read More",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
