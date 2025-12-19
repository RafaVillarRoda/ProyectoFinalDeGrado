package com.example.proyectofinaldegrado.ui.steam

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface SteamApiService {


    @GET("openid/login")
    suspend fun verifyAuthentication(@QueryMap params: Map<String, String?>): String

    @GET("/IPlayerService/getOwnedGames/v1/")
    suspend fun getOwnedGames(
        @Query("Key") apiKey: String,
        @Query("steamid") steamId: String?,
        @Query("include_appinfo") includeAppInfo: Int = 1,
        @Query("format") format: String = "json"
    ): SteamGamesResponse
}
    