package com.prafullkumar.orbit.home.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HiddenAppsDao {

    @Query("SELECT * FROM HiddenAppEntity")
    fun getAllHiddenApps(): Flow<List<HiddenAppEntity>>


    @Insert(
        onConflict = IGNORE
    )
    fun insertHiddenApp(appPackage: HiddenAppEntity)

    @Delete
    suspend fun deleteHiddenApp(appPackage: HiddenAppEntity)

}
