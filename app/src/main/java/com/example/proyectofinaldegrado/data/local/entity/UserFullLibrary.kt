package com.example.proyectofinaldegrado.data.local.entity

data class UserFullLibrary(
    val user: User,
    val books: List<Book>,
    val films: List<Film>,
    val series: List<Serie>,
    val games: List<Game>?,
    val libraryItems: List<UserLibraryItem>
)
