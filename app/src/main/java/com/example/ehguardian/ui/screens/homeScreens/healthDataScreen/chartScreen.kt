package com.example.ehguardian.ui.screens.homeScreens.dataScreen

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
import io.jetchart.common.animation.fadeInAnimation
import io.jetchart.line.Line
import io.jetchart.line.LineChart
import io.jetchart.line.renderer.line.SolidLineDrawer
import io.jetchart.line.renderer.point.FilledPointDrawer
import io.jetchart.line.renderer.xaxis.LineXAxisDrawer
import io.jetchart.line.renderer.yaxis.LineYAxisWithValueDrawer
import kotlin.random.Random

@Composable
fun ChartPage(modifier: Modifier) {
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
                    filter  // Select the new filter
                }
            }
        )

        val (lines, labels) = getLinesForFilter(selectedFilter)

        LineChart(
            lines = lines,
            labels = labels,

            modifier = Modifier
                .fillMaxSize() // This ensures the LineChart fills the entire available space
                .padding(16.dp), // Optional padding
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

            horizontalOffsetPercentage = 1f
        )
    }
}

@Composable
private fun points(count: Int) = (1..count).map {
    io.jetchart.line.Point(
        Random.nextFloat(),
        "$it"
    )
}

@Composable
private fun getLinesForFilter(filter: String): Pair<List<Line>, List<String>> {
    return when (filter) {
        "Blood Pressure" -> {
            val labels = listOf("10", "20", "30", "40", "50", "60", "70", "80", "90", "DIA")
            val lines = listOf(
                Line(
                    points = listOf(
                        io.jetchart.line.Point(120f, "90"),
                        io.jetchart.line.Point(135f, "85"),
                        io.jetchart.line.Point(110f, "80"),
                        io.jetchart.line.Point(105f, "75"),
                        io.jetchart.line.Point(140f, "70"),
                        io.jetchart.line.Point(120f, "90"),
                        io.jetchart.line.Point(135f, "85"),
                        io.jetchart.line.Point(110f, "80"),
                        io.jetchart.line.Point(105f, "75"),
                        io.jetchart.line.Point(140f, "70")
                    ),
                    lineDrawer = SolidLineDrawer(thickness = 5.dp, color = Color(0xFF5CC87C))
                )
            )
            Pair(lines, labels)
        }
        "Heart Rate" -> {
            val labels = listOf("1", "2", "3", "4", "5", "6", "7", "8")
            val lines = listOf(
                Line(
                    points = listOf(
                        io.jetchart.line.Point(72f, "1"),
                        io.jetchart.line.Point(70f, "2"),
                        io.jetchart.line.Point(68f, "3"),
                        io.jetchart.line.Point(65f, "4"),
                        io.jetchart.line.Point(67f, "5"),
                        io.jetchart.line.Point(69f, "6"),
                        io.jetchart.line.Point(71f, "7"),
                        io.jetchart.line.Point(73f, "8")
                    ),
                    lineDrawer = SolidLineDrawer(thickness = 5.dp, color = Color(0xFFEA6447))
                )
            )
            Pair(lines, labels)
        }
        "Body Mass Index" -> {
            val labels = (1..12).map { it.toString() }
            val lines = listOf(
                Line(
                    points = points(12),
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
        modifier = Modifier.padding(16.dp)
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
