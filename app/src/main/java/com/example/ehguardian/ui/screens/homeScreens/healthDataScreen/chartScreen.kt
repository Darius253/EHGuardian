package com.example.ehguardian.ui.screens.homeScreens.healthDataScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ehguardian.data.models.MeasurementData
import io.jetchart.common.animation.fadeInAnimation
import io.jetchart.line.Line
import io.jetchart.line.LineChart
import io.jetchart.line.renderer.line.SolidLineDrawer
import io.jetchart.line.renderer.point.FilledPointDrawer
import io.jetchart.line.renderer.xaxis.LineXAxisDrawer
import io.jetchart.line.renderer.yaxis.LineYAxisWithValueDrawer





@Composable
fun ChartPage(
    modifier: Modifier,
    userMeasurements: List<MeasurementData> = emptyList()
) {
    var selectedFilter by rememberSaveable { mutableStateOf("Blood Pressure") }

    Column(
        modifier = modifier
            .fillMaxHeight(0.8f)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        GraphFilter(
            selectedFilter = selectedFilter,
            onFilterChange = { filter ->
                selectedFilter = if (selectedFilter == filter) {
                    filter // Deselect if the same filter is clicked
                } else {
                    filter // Select the new filter
                }
            }
        )

        val (lines, labels) = getLinesForFilter(selectedFilter, userMeasurements)

        LineChart(
            lines = lines,
            labels = labels,
            modifier = Modifier
                .fillMaxSize() // This ensures the LineChart fills the entire available space
                .padding(10.dp), // Optional padding
            animation = fadeInAnimation(900),
            pointDrawer = FilledPointDrawer(
                color = MaterialTheme.colorScheme.primary,
                diameter = 10.dp,
            ),
            xAxisDrawer = LineXAxisDrawer(
                axisLineColor = MaterialTheme.colorScheme.onSurface,
                labelTextColor = MaterialTheme.colorScheme.onSurface,
                labelTextSize = MaterialTheme.typography.titleMedium.fontSize
            ),
            yAxisDrawer = LineYAxisWithValueDrawer(
                axisLineColor = MaterialTheme.colorScheme.onSurface,
                labelTextColor = MaterialTheme.colorScheme.onSurface,
                labelTextSize = MaterialTheme.typography.titleMedium.fontSize,
            ),

        )
    }
}

@Composable
private fun getLinesForFilter(
    filter: String,
    userMeasurements: List<MeasurementData>
): Pair<List<Line>, List<String>> {
    if (userMeasurements.isEmpty()) {
        return Pair(emptyList(), emptyList())
    }
    return when (filter) {
        "Blood Pressure" -> {
            // Reverse the userMeasurements list to display the last items first
            val reversedMeasurements = userMeasurements.reversed()

            val labels = List(reversedMeasurements.size) { index -> (index + 1).toString() }

            val points = reversedMeasurements.map { measurement ->
                io.jetchart.line.Point(
                    value = measurement.systolic.toFloat(), // Systolic as Y-axis value
                    label = measurement.timestamp, // Timestamp or any other label you want to display
                )
            }

            val lines = listOf(
                Line(
                    points = points,
                    lineDrawer = SolidLineDrawer(thickness = 5.dp, color = Color(0xFF5CC87C))
                )
            )

            Pair(lines, labels)
        }

        "Heart Rate" -> {
            // Reverse the userMeasurements list to display the last items first
            val reversedMeasurements = userMeasurements.reversed()

            val labels = List(reversedMeasurements.size) { index -> (index + 1).toString() }
            val points = reversedMeasurements.map { measurement ->
                io.jetchart.line.Point(
                    value = measurement.pulse.toFloat(),
                    label = measurement.timestamp
                )
            }
            val lines = listOf(
                Line(
                    points = points,
                    lineDrawer = SolidLineDrawer(thickness = 5.dp, color = Color(0xFFEA6447))
                )
            )
            Pair(lines, labels)
        }


        "Body Mass Index" -> {
            // Reverse the userMeasurements list to display the last items first
            val reversedMeasurements = userMeasurements.reversed()

            val labels = List(reversedMeasurements.size) { index -> (index + 1).toString() }
            val points = reversedMeasurements.map { measurement ->
                io.jetchart.line.Point(
                    value =  measurement.bmi.toFloatOrNull() ?: 0f,
                    label = measurement.timestamp
                )
            }
            val lines = listOf(
                Line(
                    points = points,
                    lineDrawer = SolidLineDrawer(thickness = 5.dp, color = Color(0xFF5B9AFD))
                )
            )
            Pair(lines, labels)
        }

        else -> Pair(emptyList(), emptyList())
    }
}


@Composable
fun GraphFilter(
    selectedFilter: String,
    onFilterChange: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(10.dp)
    ) {
        FilterCard(
            text = "Blood Pressure",
            onClick = { onFilterChange("Blood Pressure") },
            isSelected = selectedFilter == "Blood Pressure"
        )
        FilterCard(
            text = "Heart Rate",
            onClick = { onFilterChange("Heart Rate") },
            isSelected = selectedFilter == "Heart Rate"
        )
        FilterCard(
            text = "Body Mass Index",
            onClick = { onFilterChange("Body Mass Index") },
            isSelected = selectedFilter == "Body Mass Index"
        )
    }
}

@Composable
fun FilterCard(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    isSelected: Boolean,
) {
    Card(
        modifier = modifier
            .padding(5.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surface,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}



