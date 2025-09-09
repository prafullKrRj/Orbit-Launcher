package com.prafullkumar.orbit.home.data.repository

import android.content.Context
import android.widget.Toast
import com.prafullkumar.orbit.core.data.local.FavDao
import com.prafullkumar.orbit.core.data.local.FavEntity
import com.prafullkumar.orbit.core.model.AppInfo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppDrawerRepository(private val context: Context) : KoinComponent {

    private val favDao by inject<FavDao>()

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

    suspend fun removeFromFavourites(packageName: String) {
        try {
            favDao.deleteFavorite(packageName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
