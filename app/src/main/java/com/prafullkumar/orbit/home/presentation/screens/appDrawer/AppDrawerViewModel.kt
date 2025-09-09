package com.prafullkumar.orbit.home.presentation.screens.appDrawer


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.orbit.core.model.AppInfo
import com.prafullkumar.orbit.core.utils.uninstallAppMain
import com.prafullkumar.orbit.home.data.DrawerSettingsPreferenceStore
import com.prafullkumar.orbit.home.data.repository.AppDrawerRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

// Data class to hold app information

class AppDrawerViewModel() : ViewModel(), KoinComponent {

    private val appListRepository: AppDrawerRepository by inject()
    private val preferences: DrawerSettingsPreferenceStore by inject()

    val layoutType = preferences.layoutType


    fun uninstallApp(context: Context, app: AppInfo) {
        viewModelScope.launch {
            uninstallAppMain(context, app.packageName)
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