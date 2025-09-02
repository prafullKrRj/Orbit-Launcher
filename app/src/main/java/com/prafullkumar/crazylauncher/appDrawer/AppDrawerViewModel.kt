package com.prafullkumar.crazylauncher.appDrawer


import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.crazylauncher.appDrawer.drawerSettings.DrawerSettingsPreferenceStore
import com.prafullkumar.crazylauncher.appDrawer.drawerSettings.DrawerSettingsViewModel
import com.prafullkumar.crazylauncher.domain.AppInfo
import com.prafullkumar.crazylauncher.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

// Data class to hold app information

class AppDrawerViewModel(private val applicationContext: Context) : ViewModel(), KoinComponent {


    private val preferences: DrawerSettingsPreferenceStore by inject()

    val layoutType = preferences.layoutType

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()


    private val appRepository = AppRepository(applicationContext)

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())

    val groupedApps: StateFlow<Map<Char, List<AppInfo>>> = _installedApps
        .filter {
            it.any { entry ->
                searchQuery.value.isEmpty() ||
                        entry.label.contains(searchQuery.value, ignoreCase = true)
            }
        }
        .map { apps ->
            apps.groupBy { it.label.first().uppercaseChar() }
                .toSortedMap()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )


    init {
        loadInstalledApps()
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            try {
                val apps = appRepository.getInstalledApps()
                _installedApps.value = apps
            } catch (e: Exception) {
                e.printStackTrace()
                _installedApps.value = emptyList()
            }
        }
    }

    fun launchApp(context: Context, appInfo: AppInfo) {
        appInfo.launchIntent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Important for launching from non-Activity context
            try {
                context.startActivity(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.update {
            newQuery
        }
    }
}