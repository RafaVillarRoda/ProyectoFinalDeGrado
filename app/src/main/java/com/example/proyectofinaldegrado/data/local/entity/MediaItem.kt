package com.example.proyectofinaldegrado.data.local.entity

sealed interface MediaItem {

    val title: String
    val author: String?
    val genre: String?
    val dur: Int

    val poster: String?

}


