package com.prafullkumar.orbit.home.presentation.screens.hiddenApps.addPassword

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.prafullkumar.orbit.home.data.repository.HiddenAppsRepository


class ChangePasswordViewModel(
    private val repository: HiddenAppsRepository
) : ViewModel() {

    var newPassword by mutableStateOf("")


    fun udpatePassword() {
        repository.savePassword(newPassword)
    }

    fun verifyPassword(): Boolean {
        return true
    }

}