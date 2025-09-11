package com.prafullkumar.orbit.home.data.repository

import android.content.Context
import com.prafullkumar.orbit.core.data.local.fav.FavDao
import com.prafullkumar.orbit.core.data.local.fav.FavEntity
import com.prafullkumar.orbit.core.data.local.installedApps.InstalledAppsDao
import com.prafullkumar.orbit.core.model.AppInfo
import com.prafullkumar.usage.data.UsageDetails
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent

class HomeRepository(
    private val dao: FavDao,
    private val installedAppDao: InstalledAppsDao,
    private val context: Context
) : KoinComponent {

    fun getAllFavourites(): Flow<List<AppInfo>> {
        return dao.getAllFavorites().map { list ->
            list.map { favEntity ->
                favEntity.toAppInfo(context)
            }
        }
    }

    suspend fun removeFromFavourites(currentApp: String) {
        dao.deleteFavorite(currentApp)
    }

    fun getInstalledApps(): Flow<List<AppInfo>> {
        return installedAppDao.getAllInstalledApps().map { it -> it.map { it.toAppInfo(context) } }
    }

    fun getTotalTimeSpent(): Flow<Long> {
        return flow {
            while (true) {
                val totalTime = UsageDetails.getCurrentDayPhoneUsageData(
                    context,
                    installedAppDao.getInstalledAppsList().map { it.packageName }.toSet()
                )
                emit(totalTime)
                delay(120000) // 2 minutes
            }
        }
    }
}

fun FavEntity.toAppInfo(context: Context): AppInfo {
    val launchIntent = context.packageManager.getLaunchIntentForPackage(this.packageName)
    val icon = context.packageManager.getApplicationIcon(this.packageName)
    return AppInfo(
        label = this.label,
        packageName = this.packageName,
        icon = icon,
        launchIntent = launchIntent
    )
}