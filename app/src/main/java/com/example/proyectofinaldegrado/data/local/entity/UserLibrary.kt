package com.example.proyectofinaldegrado.data.local.entity

import androidx.room.Entity
import kotlinx.datetime.Instant

@Entity(
    tableName = "user_library",
    primaryKeys = ["userName", "itemId", "itemType"]
)
data class UserLibraryItem(
    val userName: String,
    val itemId: String,
    val itemType: String,
    val rating: Int,
    val additionDate: Instant?

)
