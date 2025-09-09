package com.prafullkumar.orbit.core.data.usageData

import android.app.usage.UsageEvents.Event
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Context.USAGE_STATS_SERVICE
import android.content.pm.PackageManager
import android.util.Log
import com.prafullkumar.orbit.core.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend fun getTodayHourlyUsage(context: Context, installedPackageNames: Set<String>): List<Pair<Int, Long>> =
    withContext(Dispatchers.IO) {
        val usageStatsManager = context.getSystemService(USAGE_STATS_SERVICE) as? UsageStatsManager
            ?: return@withContext (0..23).map { hour -> Pair(hour, 0L) }

        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        calendar.set(java.util.Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis

        try {
            val hourlyUsage = LongArray(24)
            val events = usageStatsManager.queryEvents(startOfDay, endOfDay)
            val event = Event()

            val sessionStartMap = mutableMapOf<String, Long>()

            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                if (!installedPackageNames.contains(event.packageName) || event.packageName == "com.prafullkumar.orbit") continue

                when (event.eventType) {
                    Event.MOVE_TO_FOREGROUND -> {
                        // Record start of a foreground session
                        sessionStartMap[event.packageName] = event.timeStamp
                    }
                    Event.MOVE_TO_BACKGROUND -> {
                        val start = sessionStartMap.remove(event.packageName)
                        if (start != null && event.timeStamp > start) {
                            addSessionToHours(start, event.timeStamp, hourlyUsage)
                        }
                    }
                }
            }

            // Handle still-open foreground sessions up to now (clamped to endOfDay)
            val now = System.currentTimeMillis().coerceAtMost(endOfDay)
            sessionStartMap.forEach { (_, start) ->
                if (now > start) addSessionToHours(start, now, hourlyUsage)
            }

            Log.d("getTodayHourlyUsage", "Hourly ms usage: ${hourlyUsage.contentToString()}")
            return@withContext hourlyUsage.mapIndexed { hour, ms -> hour to ms }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext (0..23).map { it to 0L }
        }
    }

// Rewritten: splits a session across hour boundaries precisely.
private fun addSessionToHours(startTime: Long, endTime: Long, hourlyUsage: LongArray) {
    if (endTime <= startTime) return
    var cursor = startTime
    while (cursor < endTime) {
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = cursor }
        val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
        if (hour !in 0..23) break

        // Start of next hour
        val nextHourStart = java.util.Calendar.getInstance().apply {
            timeInMillis = cursor
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
            add(java.util.Calendar.HOUR_OF_DAY, 1)
        }.timeInMillis

        val segmentEnd = minOf(endTime, nextHourStart)
        val delta = segmentEnd - cursor
        if (delta > 0) hourlyUsage[hour] += delta
        cursor = segmentEnd
    }
}

suspend fun getInstalledAppsMain(context: Context): List<AppInfo> = withContext(Dispatchers.IO) {
    try {
        val pm: PackageManager = context.packageManager
        val applications = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        val appList = mutableListOf<AppInfo>()
        for (appInfo in applications) {
            if (pm.getLaunchIntentForPackage(appInfo.packageName) != null) {
                val label = pm.getApplicationLabel(appInfo).toString()
                val packageName = appInfo.packageName
                val icon = pm.getApplicationIcon(appInfo)
                val launchIntent = pm.getLaunchIntentForPackage(appInfo.packageName)

                appList.add(AppInfo(label, packageName, icon, launchIntent))
            }
        }
        appList.sortedBy { it.label.lowercase() }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}