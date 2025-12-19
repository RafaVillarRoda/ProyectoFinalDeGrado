package com.example.proyectofinaldegrado.ui.steam

import android.util.Log




object SteamRepository {
    private val apiService = SteamApiClient.instance


    suspend fun verifyUserAuthentication(params: Map<String, String?>): String {
        return apiService.verifyAuthentication(params)
    }
    suspend fun getOwnedGames(steamId: String?, userApiKey: String): List<SteamGame> {
        return try {
            val response = apiService.getOwnedGames( userApiKey, steamId)
            response.response.games ?: emptyList()
        }catch (e : Exception){
            Log.e("SteamRepository", "Error al obtener juegos", e)
            emptyList()
        }
    }
}
    