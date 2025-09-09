package com.prafullkumar.orbit.usageScreen.data

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.prafullkumar.orbit.core.data.usageData.getTodayHourlyUsage
import com.prafullkumar.orbit.core.utils.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import java.util.Calendar

class UsageRepository(
    private val context: Context
) : KoinComponent {

    suspend fun getUsageData(startTime: Long, endTime: Long): Response<List<AppUsageData>> {
        try {
            val usageStats =
                context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

            val stats = usageStats.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, startTime, endTime
            )

            val pm = context.packageManager
            val launchable = withContext(Dispatchers.IO) {
                pm.getInstalledApplications(PackageManager.GET_META_DATA)
                    .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
                    .map { it.packageName }
                    .toSet()
            }

            val filtered = stats.asSequence()
                .filter { it.packageName != "com.prafullkumar.orbit" }
                .filter { launchable.contains(it.packageName) }
                .filter { it.lastTimeUsed in startTime..endTime }
                .filter { it.totalTimeInForeground > 0 }
                .groupBy { it.packageName }
                .mapNotNull { (_, list) -> list.maxByOrNull { it.lastTimeUsed } }
                .sortedByDescending { it.totalTimeInForeground }
                .map { it.toAppUsageData(context) }
                .toList()

            return Response.Success(filtered)
        } catch (e: Exception) {
            Log.e("UsageRepository", "getUsageData error", e)
            return Response.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getUsageDataForToday(): Response<List<AppUsageData>> {
        // Calculate start of today
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        // Calculate end of today
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endTime = calendar.timeInMillis

        return getUsageData(startTime, endTime)
    }

    suspend fun getHourlyUsageDataForToday(installedApps: Set<String>): List<Pair<Int, Double>> {
        val rawMsPerHour = getTodayHourlyUsage(context, installedApps)
        val minutes = rawMsPerHour.map { (h, ms) -> h to (ms / 60000.0) }
        val completed = (0..23).map { h -> h to (minutes.find { it.first == h }?.second ?: 0.0) }
        Log.d("UsageRepository", "Hourly minutes: $completed")
        return completed
    }
}