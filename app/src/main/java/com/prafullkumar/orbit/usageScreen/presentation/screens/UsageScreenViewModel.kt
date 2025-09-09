package com.prafullkumar.orbit.usageScreen.presentation.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.orbit.core.data.InstalledAppsCaches
import com.prafullkumar.orbit.core.utils.Response
import com.prafullkumar.orbit.usageScreen.data.AppUsageData
import com.prafullkumar.orbit.usageScreen.data.UsageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.roundToLong

data class UsageScreenUiState(
    val usageStats: List<AppUsageData> = emptyList(),
    val loading: Boolean = false,
    val error: Boolean = false
)

class UsageScreenViewModel(
    private val repository: UsageRepository
) : ViewModel(), KoinComponent {

    private val installedAppsCaches by inject<InstalledAppsCaches>()
    val installedApps = installedAppsCaches.getInstalledApps().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000), emptyList()
    )
    private val _uiState = MutableStateFlow(UsageScreenUiState())
    val uiState = _uiState.asStateFlow()
    var hourlyUsage by mutableStateOf<List<Pair<Int, Double>>>(emptyList())
    var totalUsage by mutableLongStateOf(0L)

    init {
        // Wait for installed apps before loading usage to avoid empty initial set
        viewModelScope.launch {
            installedApps.collect { apps ->
                if (apps.isNotEmpty()) {
                    getUsageStats()
                    getHourlyUsageData()
                }
            }
        }
    }

    fun getHourlyUsageData() {
        viewModelScope.launch {
            val pkgs = installedApps.value.map { it.packageName }.toSet()
            if (pkgs.isEmpty()) return@launch
            hourlyUsage = repository.getHourlyUsageDataForToday(pkgs)
            totalUsage = hourlyUsage.sumOf { it.second }.roundToLong() // total minutes (rounded)
            Log.d("UsageScreenViewModel", "Hourly Usage (minutes): $hourlyUsage total=$totalUsage")
        }
    }

    fun getUsageStats() {
        _uiState.update {
            it.copy(
                loading = true,
                error = false,
                usageStats = emptyList()
            )
        }
        viewModelScope.launch {
            val response = repository.getUsageDataForToday()
            when (response) {
                is Response.Error -> {
                    _uiState.update {
                        it.copy(
                            error = true,
                            loading = false,
                            usageStats = emptyList()
                        )
                    }
                }

                Response.Loading -> {
                    _uiState.update {
                        it.copy(
                            loading = true,
                            error = false,
                            usageStats = emptyList()
                        )
                    }
                }

                is Response.Success<List<AppUsageData>> -> {
                    _uiState.update {
                        it.copy(
                            usageStats = response.data,
                            loading = false,
                            error = false
                        )
                    }

                }
            }
        }
    }
}