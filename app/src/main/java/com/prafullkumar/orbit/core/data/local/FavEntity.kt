package com.prafullkumar.orbit.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavEntity(
    @PrimaryKey(autoGenerate = false)
    val packageName: String,
    val label: String,
)