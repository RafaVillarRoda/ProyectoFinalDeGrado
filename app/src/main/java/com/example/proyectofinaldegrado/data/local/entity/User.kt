package com.example.proyectofinaldegrado.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant


@Entity(tableName = "users")
data class User(
    @PrimaryKey
    @ColumnInfo(name = "user_name")
    val userName: String,


    @ColumnInfo(name = "password_hash")
    val passwordHash: String,

    @ColumnInfo(name = "start_date")
    val startDate: Instant,

    @ColumnInfo(name = "SteamID")
    var steamID: String? = null,


    )
