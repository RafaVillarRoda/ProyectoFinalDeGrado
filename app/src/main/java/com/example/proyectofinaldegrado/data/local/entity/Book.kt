package com.example.proyectofinaldegrado.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.proyectofinaldegrado.data.local.entity.MediaItem
import kotlinx.datetime.Instant

@Entity(tableName = "books")
data class Book(
    @PrimaryKey
    @ColumnInfo(name = "book_title")
    val bookTitle: String,

    @ColumnInfo(name = "author")
    override val author: String?,

    @ColumnInfo(name = "genre")
    override val genre: String?,

    @ColumnInfo(name = "pages")
    val pages: String?,

    @ColumnInfo(name = "rating")
    var rating: Int,

    @ColumnInfo(name = "release_date")
    val releaseDate: String?,

    @ColumnInfo(name = "addition_date")
    var additionDate: Instant?,

): MediaItem {
    override val title: String
        get() = bookTitle
    val creator: String
        get() = author ?: ""
    val genr: String
        get() = genre ?: ""
    override val dur: Int
        get() = pages?.toIntOrNull() ?: 0

}


