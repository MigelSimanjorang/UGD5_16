package com.example.toko.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Buy (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val note: String,
)