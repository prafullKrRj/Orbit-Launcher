package com.prafullkumar.crazylauncher.appDrawer.presentation


import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.crazylauncher.appDrawer.data.DrawerSettingsPreferenceStore
import com.prafullkumar.crazylauncher.appDrawer.data.repository.AppDrawerRepository
import com.prafullkumar.crazylauncher.core.model.AppInfo
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

    private val appListRepository: AppDrawerRepository by inject()
    private val preferences: DrawerSettingsPreferenceStore by inject()

    val layoutType = preferences.layoutType

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()


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
                val apps = appListRepository.getInstalledApps()
                _installedApps.value = apps
            } catch (e: Exception) {
                e.printStackTrace()
                _installedApps.value = emptyList()
            }
        }
    }



    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.update {
            newQuery
        }
    }

    fun addToFavorites(app: AppInfo) {
        viewModelScope.launch {
            try {
                appListRepository.addToFavourites(app)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}