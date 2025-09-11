package com.prafullkumar.hiddenapps.presentation.setPasswordScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.hiddenapps.data.HiddenAppsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SetPasswordViewModel : ViewModel(), KoinComponent {
    private val repository: HiddenAppsRepository by inject()

    /**
     * Sets new password
     */
    fun setPassword(password: String, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setPassword(password)
            onComplete()
        }
    }


}