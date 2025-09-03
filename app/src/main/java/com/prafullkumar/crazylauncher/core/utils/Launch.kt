package com.prafullkumar.crazylauncher.core.utils

import android.content.Context
import android.content.Intent
import com.prafullkumar.crazylauncher.core.model.AppInfo

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