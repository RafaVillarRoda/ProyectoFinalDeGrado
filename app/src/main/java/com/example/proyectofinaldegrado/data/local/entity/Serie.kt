package com.example.proyectofinaldegrado.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "series")
data class Serie(
    @PrimaryKey
    @ColumnInfo(name = "serie_title")
    val serieTitle: String,

    @ColumnInfo(name = "release_date")
    val releaseDate: String?,

    @ColumnInfo(name = "director")
    val director: String?,

    @ColumnInfo(name = "genre")
    override val genre: String?,

    @ColumnInfo(name = "chapters")
    val chapters: String?,

    @ColumnInfo(name = "rating")
    val rating: String?,

    @ColumnInfo(name = "addition_date")
    val additionDate: String?,


    ):MediaItem {
    override val title: String
        get() = serieTitle
    override val author: String?
        get() = director
    val genr: String?
        get() = genre
    override val dur: Int
        get() = chapters?.toIntOrNull() ?: 0

}
