package com.prafullkumar.crazylauncher.appDrawer.presentation.drawerSettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.crazylauncher.appDrawer.data.DrawerSettingsPreferenceStore
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DrawerSettingsViewModel : ViewModel(), KoinComponent {

    private val prefStore by inject<DrawerSettingsPreferenceStore>()

    val layoutType = prefStore.layoutType
    val sortingOrder = prefStore.sortingOrder

    init {

    }

    fun changeLayoutType(type: LayoutType) {
        viewModelScope.launch {
            prefStore.setLayoutType(type)
        }
    }

    fun changeSortingOrder(order: SortingOrder) {
        viewModelScope.launch {
            prefStore.setSortingOrder(order)
        }
    }
}