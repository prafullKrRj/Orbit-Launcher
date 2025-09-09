package com.prafullkumar.orbit.home.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HiddenAppEntity(
    @PrimaryKey
    val packageName: String,
    val label: String
)