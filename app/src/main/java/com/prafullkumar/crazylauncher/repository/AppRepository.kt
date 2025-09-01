package com.prafullkumar.crazylauncher.repository

import android.content.Context
import android.content.pm.PackageManager
import com.prafullkumar.crazylauncher.domain.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(private val context: Context) {

    suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        try {
            val pm: PackageManager = context.packageManager
            val applications = pm.getInstalledApplications(PackageManager.GET_META_DATA)

            val appList = mutableListOf<AppInfo>()
            for (appInfo in applications) {
                if (pm.getLaunchIntentForPackage(appInfo.packageName) != null) {
                    val label = pm.getApplicationLabel(appInfo).toString()
                    val packageName = appInfo.packageName
                    val icon = pm.getApplicationIcon(appInfo)
                    val launchIntent = pm.getLaunchIntentForPackage(appInfo.packageName)

                    appList.add(AppInfo(label, packageName, icon, launchIntent))
                }
            }
            appList.sortedBy { it.label.lowercase() }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
