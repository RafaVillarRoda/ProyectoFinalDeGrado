package com.example.proyectofinaldegrado.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo


@Entity (tableName = "Games", primaryKeys = ["appid", "user_name"])
data class Game(


    @ColumnInfo(name = "appid")
    val appid: Int,


    @ColumnInfo(name = "user_name")
    val userName: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "playtime_forever")
    val playtimeForever: Int,

    @ColumnInfo(name = "img_icon_url")
    val imgIconUrl: String?,

):  MediaItem {
    override val title: String
        get() = name
    override val author: String
        get() = ""
    override val genre: String
        get() = ""
    override val dur: Int
        get() = playtimeForever
    override val poster: String?
        get() = imgIconUrl
}