package com.prafullkumar.orbit.home.data.repository

import android.content.Context
import androidx.core.content.edit
import com.prafullkumar.orbit.core.utils.Encryption
import com.prafullkumar.orbit.home.data.local.HiddenAppsDao
import org.koin.core.component.KoinComponent

class HiddenAppsRepository(
    private val dao: HiddenAppsDao,
    private val context: Context
) : KoinComponent {

    private val sharedPref = context.getSharedPreferences("hidden_apps_prefs", Context.MODE_PRIVATE)

    companion object {
        const val STORED_PASSWORD_HASH = "stored_password"
    }

    fun savePassword(pin: String) {
        Encryption.storePassword(pin).also { encryptedPin ->
            sharedPref.edit { putString(STORED_PASSWORD_HASH, encryptedPin) }
        }
    }

    fun verifyPassword(inputPin: String): Boolean {
        val storedHash = sharedPref.getString(STORED_PASSWORD_HASH, null) ?: return false
        return Encryption.verifyPassword(storedHash, inputPin)
    }


    fun getHiddenApps() {

    }

}