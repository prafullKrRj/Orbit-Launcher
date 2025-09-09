package com.prafullkumar.orbit.home.data.local

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [HiddenAppEntity::class],
    version = 1,
    exportSchema = false
)
abstract class HiddenAppsDatabase : RoomDatabase() {

    abstract fun hiddenAppsDao(): HiddenAppsDao
}