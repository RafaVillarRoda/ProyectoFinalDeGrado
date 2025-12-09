package com.example.proyectofinaldegrado.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "films")
data class Film(
    @PrimaryKey
    @ColumnInfo(name = "film_title")
    val filmTitle: String,

    @ColumnInfo(name = "release_date")
    val releaseDate: String,

    @ColumnInfo(name = "director")
    val director: String,

    @ColumnInfo(name = "genre")
    override val genre: String,

    @ColumnInfo(name = "duration")
    val duration: String,

    @ColumnInfo(name = "rating")
    val rating: String?,

    @ColumnInfo(name = "addition_date")
    val additionDate: String?,
):MediaItem {
    override val title: String
    get() = filmTitle
    override val author: String
    get() = director
    val genr: String
    get() = genre
    override val dur: Int
    get() = duration.toIntOrNull() ?: 0

}
