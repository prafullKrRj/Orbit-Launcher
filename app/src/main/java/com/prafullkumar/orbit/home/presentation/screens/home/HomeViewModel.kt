package com.prafullkumar.orbit.home.presentation.screens.home

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.orbit.core.data.InstalledAppsCaches
import com.prafullkumar.orbit.core.data.usageData.getTodayHourlyUsage
import com.prafullkumar.orbit.core.model.AppInfo
import com.prafullkumar.orbit.core.utils.uninstallAppMain
import com.prafullkumar.orbit.home.data.repository.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.max

class HomeViewModel(
    private val context: Context, private val repository: HomeRepository,
) : ViewModel(), KoinComponent {
    private val installedAppsCaches by inject<InstalledAppsCaches>()
    val installedApps = installedAppsCaches.getInstalledApps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    var currentUsage by mutableLongStateOf(0L)
    val favApps = repository.getAllFavourites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val groupedApps: StateFlow<Map<Char, List<AppInfo>>> =
        installedApps.map { apps ->
            apps.groupBy { it.label.first().uppercaseChar() }.toSortedMap()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    init {
        viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                // getTodayHourlyUsage returns milliseconds per hour; convert to total minutes here
                val totalMs = getTodayHourlyUsage(
                    context,
                    installedApps.value.map { it.packageName }.toSet()
                ).sumOf { it.second }
                currentUsage = max(0L, totalMs / 60000L) // minutes
                delay(60000)
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

    fun uninstallApp(context: Context, currentApp: String) {
        viewModelScope.launch {
            try {
                launch { repository.removeFromFavourites(currentApp) }
                // Immediately remove from cache to update UI
//                launch { installedAppsCaches.removeFromPackageName(currentApp) }
                launch { uninstallAppMain(context, currentApp) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun removeFromFavorites(packageName: String) {
        viewModelScope.launch {
            repository.removeFromFavourites(packageName)
        }
    }

    fun launchCamera(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage("com.android.camera")
        intent?.let {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } ?: run {
            Toast.makeText(context, "No camera app found", Toast.LENGTH_SHORT).show()
        }
    }
}