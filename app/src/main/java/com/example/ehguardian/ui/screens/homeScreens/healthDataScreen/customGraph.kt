package com.example.ehguardian.ui.screens.homeScreens.healthDataScreen



import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import com.example.ehguardian.data.models.MeasurementData

@Composable
fun BloodPressureGraph(
    modifier: Modifier = Modifier,
    userMeasurements: List<MeasurementData> = emptyList()
) {
    // Define the specific ranges for diastolic (X-axis) and systolic (Y-axis) values
    val minDiastolic = 70f
    val maxDiastolic = 100f
    val minSystolic = 100f
    val maxSystolic = 140f

    // Add padding for the axis and labels
    val padding = 40f
    val labelPadding = 20f

    // Canvas to draw the graph
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Draw X and Y axes
        drawLine(
            color = Color.Black,
            start = androidx.compose.ui.geometry.Offset(padding, height - padding),
            end = androidx.compose.ui.geometry.Offset(width - padding, height - padding),
            strokeWidth = 5f
        ) // X-axis (Diastolic)

        drawLine(
            color = Color.Black,
            start = androidx.compose.ui.geometry.Offset(padding, height - padding),
            end = androidx.compose.ui.geometry.Offset(padding, padding),
            strokeWidth = 5f
        ) // Y-axis (Systolic)

        // Draw labels for X (Diastolic) and Y (Systolic) axes
        val paint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            textSize = 30f
            color = android.graphics.Color.BLACK
        }

        // Draw X-axis (Diastolic) labels: 60, 70, 80, 90, 100
        val diastolicLabels = listOf(70, 80, 90, 100)
        for (i in diastolicLabels.indices) {
            val diastolicValue = diastolicLabels[i]
            val xPosition = padding + i * (width - 2 * padding) / (diastolicLabels.size - 1)
            drawContext.canvas.nativeCanvas.drawText(
                diastolicValue.toString(),
                xPosition,
                height - padding + labelPadding,
                paint
            )
        }

        // Draw Y-axis (Systolic) labels: 110, 120, 130, 140
        val systolicLabels = listOf(100,110, 120, 130, 140)
        for (i in systolicLabels.indices) {
            val systolicValue = systolicLabels[i]
            val yPosition = height - padding - i * (height - 2 * padding) / (systolicLabels.size - 1)
            drawContext.canvas.nativeCanvas.drawText(
                systolicValue.toString(),
                padding - labelPadding * 1.5f,
                yPosition,
                paint
            )
        }

        // Plot the data points (Diastolic vs Systolic)
        val dataPoints = userMeasurements.map { measurement ->
            val x = padding + (measurement.diastolic.toFloat() - minDiastolic) / (maxDiastolic - minDiastolic) * (width - 2 * padding)
            val y = height - padding - (measurement.systolic.toFloat() - minSystolic) / (maxSystolic - minSystolic) * (height - 2 * padding)
            androidx.compose.ui.geometry.Offset(x, y)
        }

        // Draw lines connecting the data points
        for (i in 0 until dataPoints.size - 1) {
            drawLine(
                color = Color.Red,
                start = dataPoints[i],
                end = dataPoints[i + 1],
                strokeWidth = 3f
            )
        }

        // Draw the data points
        dataPoints.forEach { point ->
            drawCircle(
                color = Color.Blue,
                radius = 8f,
                center = point
            )
        }
    }
}

