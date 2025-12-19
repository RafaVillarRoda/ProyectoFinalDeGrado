package com.example.proyectofinaldegrado.data.local.dao


import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.proyectofinaldegrado.data.local.entity.Game
@Dao
interface SteamGamesDao {
    @Query("SELECT * FROM games")
    suspend fun getAllSteamGames(): List<Game>

    @Query("SELECT * FROM games WHERE appid = :appid")
    suspend fun getSteamGameById(appid: List<String>): List<Game>


    @Upsert
    suspend fun upsertSteamGame(games: List<Game>)

    @Query("DELETE FROM games WHERE user_name = :userName")
    suspend fun deleteAllSteamGames(userName: String)

    @Query("SELECT * FROM Games WHERE name IN (:names)")
    suspend fun getGamesByNames(names: List<String>): List<Game>

}



