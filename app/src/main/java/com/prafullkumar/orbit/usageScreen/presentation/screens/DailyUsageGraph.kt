package com.prafullkumar.orbit.usageScreen.presentation.screens

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars

@Composable
fun DailyUsageDetails(viewModel: UsageScreenViewModel) {
    val totalMinutes = viewModel.totalUsage
    Text(
        text = buildString {
            if (totalMinutes >= 60) {
                val hours = totalMinutes / 60
                val minutes = totalMinutes % 60
                append("${hours}h ${minutes}m")
            } else append("${totalMinutes}m")
        },
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    PhoneUsageColumnChart(
        data = viewModel.hourlyUsage,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
    )
}

@Composable
fun PhoneUsageColumnChart(
    data: List<Pair<Int, Double>>,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    isSystemInDarkTheme()
    LaunchedEffect(Unit) {
        Log.d("DailyUsageGraph", data.toString())
    }

    val completeData = (0..23).map { hour ->
        val existingData = data.find { it.first == hour }
        hour to (existingData?.second ?: 0.0)
    }

    val maxMinutes = (completeData.maxOfOrNull { it.second } ?: 0.0)
    val chartMax = if (maxMinutes <= 60) 60.0 else ( ( (maxMinutes / 30).toInt() + 1) * 30 ).toDouble()

    if (completeData.isNotEmpty()) {
        ColumnChart(
            modifier = modifier.height(300.dp),
            data = listOf(
                Bars(
                    values = completeData.map { (hour, usage) ->
                        Bars.Data(
                            value = usage,
                            color = Brush.verticalGradient(
                                colors = listOf(
                                    primary,
                                    primaryContainer
                                )
                            )
                        )
                    },
                    label = "Usage (minutes)"
                )
            ),
            barProperties = BarProperties(
                spacing = 2.dp
            ),
            maxValue = chartMax
        )
    }
}