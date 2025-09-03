package com.prafullkumar.crazylauncher.home.presentation

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.icu.util.Calendar
import android.os.Build
import android.provider.AlarmClock
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.crazylauncher.home.data.HomeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    context: Context,
    private val repository: HomeRepository
) : ViewModel() {
    var currentUsage by mutableLongStateOf(getTodaysAppUsage(context))
    val favApps = repository.getAllFavourites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    init {
        viewModelScope.launch {
            while (true) {
                currentUsage = getTodaysAppUsage(context)
                delay(60000) // Update every minute
            }
        }
    }

    fun launchClockApp(context: Context) {
        try {
            val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Required when starting activity from non-activity context
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No clock app found", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchCalendarApp(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_APP_CALENDAR)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Required when starting activity from non-activity context
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No calendar app found", Toast.LENGTH_SHORT).show()
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
                val homeIntent = Intent(Intent.ACTION_MAIN)
                homeIntent.addCategory(Intent.CATEGORY_HOME)
                val homeActivities = packageManager.queryIntentActivities(homeIntent, 0)
                val isLauncher =
                    homeActivities.any { it.activityInfo.packageName == stat.packageName }

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