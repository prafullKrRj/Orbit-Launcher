package com.prafullkumar.crazylauncher.appDrawer


import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.crazylauncher.domain.AppInfo
import com.prafullkumar.crazylauncher.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Data class to hold app information

class AppDrawerViewModel(private val applicationContext: Context) : ViewModel() {

    private val appRepository = AppRepository(applicationContext)

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()
    
    val groupedApps: StateFlow<Map<Char, List<AppInfo>>> = _installedApps
        .map { apps ->
            apps.groupBy { it.label.first().uppercaseChar() }
                .toSortedMap()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadInstalledApps()
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val apps = appRepository.getInstalledApps()
                _installedApps.value = apps
            } catch (e: Exception) {
                e.printStackTrace()
                _installedApps.value = emptyList()
            } finally {
                _isLoading.value = false
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
}