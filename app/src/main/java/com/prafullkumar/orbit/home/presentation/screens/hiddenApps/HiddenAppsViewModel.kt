package com.prafullkumar.orbit.home.presentation.screens.hiddenApps

import androidx.lifecycle.ViewModel
import com.prafullkumar.orbit.home.data.repository.HiddenAppsRepository

class HiddenAppsViewModel(
    private val repository: HiddenAppsRepository
) : ViewModel() {

    val hiddenApps = repository.getHiddenApps()


}
