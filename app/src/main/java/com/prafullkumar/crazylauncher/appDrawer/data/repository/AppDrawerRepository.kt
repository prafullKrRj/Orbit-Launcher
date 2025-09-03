package com.prafullkumar.crazylauncher.appDrawer.data.repository

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import com.prafullkumar.crazylauncher.core.data.local.FavDao
import com.prafullkumar.crazylauncher.core.data.local.FavEntity
import com.prafullkumar.crazylauncher.core.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppDrawerRepository(private val context: Context) : KoinComponent {

    private val favDao by inject<FavDao>()
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

    suspend fun addToFavourites(app: AppInfo) {
        val count = favDao.getFavoriteCount()
        if (count >= 7) {
            Toast.makeText(context, "You can only add up to 7 favorites", Toast.LENGTH_SHORT).show()
            return
        }
        favDao.insertFavorite(
            FavEntity(
                packageName = app.packageName, label = app.label
            )
        )
    }

}
