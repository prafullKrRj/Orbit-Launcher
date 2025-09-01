package com.prafullkumar.crazylauncher.home

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.icu.util.Calendar
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel(
    context: Context
) : ViewModel() {
    var currentUsage by mutableLongStateOf(getTodaysAppUsage(context))

    init {
        viewModelScope.launch {
            while (true) {
                currentUsage = getTodaysAppUsage(context)
                delay(60000) // Update every minute
            }
        }
    }
}

fun getTodaysAppUsage(context: Context): Long {
    val usageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    val cal = Calendar.getInstance()
    // Set to the beginning of today (midnight)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    val startTime = cal.timeInMillis

    val endTime = System.currentTimeMillis()

    // Get individual app usage stats instead of daily aggregates
    val usageStats = usageStatsManager.queryUsageStats(
        UsageStatsManager.INTERVAL_BEST,
        startTime,
        endTime
    )

    val packageManager = context.packageManager

    return usageStats
        .filter { stat ->
            try {
                val appInfo = packageManager.getApplicationInfo(stat.packageName, 0)
                val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                // Check if app is a launcher by looking for HOME category
                val homeIntent = android.content.Intent(android.content.Intent.ACTION_MAIN)
                homeIntent.addCategory(android.content.Intent.CATEGORY_HOME)
                val homeActivities = packageManager.queryIntentActivities(homeIntent, 0)
                val isLauncher = homeActivities.any { it.activityInfo.packageName == stat.packageName }

                // Include only user apps that are not system apps, not launchers, and have usage
                !isSystemApp && !isLauncher && stat.totalTimeInForeground > 0 &&
                        packageManager.getLaunchIntentForPackage(stat.packageName) != null
            } catch (e: Exception) {
                false
            }
        }
        .sumOf { stat ->
            // Use the appropriate time field based on Android version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android 10+, use totalTimeVisible which is more accurate
                stat.totalTimeVisible
            } else {
                stat.totalTimeInForeground
            }
        }
}