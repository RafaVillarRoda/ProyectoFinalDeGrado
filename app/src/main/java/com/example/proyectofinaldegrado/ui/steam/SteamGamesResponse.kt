package com.example.proyectofinaldegrado.ui.steam

data class SteamGamesResponse(
    val response: GameList
)

data class GameList(
    val game_count: Int,
    val games: List<SteamGame>?
)

data class SteamGame(
    val appid: Int,
    val name: String,
    val playtime_forever: Int,
    val img_icon_url: String?,
)
