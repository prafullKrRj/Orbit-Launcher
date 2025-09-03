package com.prafullkumar.crazylauncher.home.data

import android.content.Context
import com.prafullkumar.crazylauncher.core.data.local.FavDao
import com.prafullkumar.crazylauncher.core.data.local.FavEntity
import com.prafullkumar.crazylauncher.core.model.AppInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent

class HomeRepository(
    private val dao: FavDao,
    private val context: Context
) : KoinComponent {

    fun getAllFavourites(): Flow<List<AppInfo>> {
        return dao.getAllFavorites().map { list ->
            list.map { favEntity ->
                favEntity.toAppInfo(context)
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