package com.prafullkumar.crazylauncher.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [FavEntity::class], version = 1, exportSchema = true)
abstract class FavDatabase : RoomDatabase() {

    abstract fun favDao(): FavDao
}

