package com.example.proyectofinaldegrado.ui.steam

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.proyectofinaldegrado.data.local.AppDatabase
import com.example.proyectofinaldegrado.data.repository.UserRepository
import com.example.proyectofinaldegrado.viewmodels.mainActivity.MainViewModel
import com.example.proyectofinaldegrado.viewmodels.mainActivity.MainViewModelFactory
import java.util.regex.Pattern

class SteamAuthActivity : ComponentActivity() {

    private val database by lazy {
        AppDatabase.getDatabase(
            this,
            scope = this.lifecycleScope

        )
    }

    private val userRepository by lazy {
        UserRepository(
            database.userDao(),
            database.bookDao(),
            database.filmDao(),
            database.serieDao(),
            database.steamGamesDao(),
        )
    }
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory(userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = intent.data

        val expectedUrlStart = "https://rafavillarroda.github.io/ProyectoFinalDeGrado/auth"

        if (uri != null && uri.toString().startsWith(expectedUrlStart)) {
            val mode = uri.getQueryParameter("openid.mode")
            val claimedId = uri.getQueryParameter("openid.claimed_id")

            if (mode == "id_res" && !claimedId.isNullOrEmpty()) {
                val steamId = extractSteamIdFromUrl(claimedId)

                if (steamId != null) {
                    Log.d("SteamAuth", "¡Verificación exitosa! SteamID: $steamId")
                    Toast.makeText(this, "Cuenta de Steam vinculada: $steamId", Toast.LENGTH_LONG)
                        .show()

                    mainViewModel.updateSteamId(steamId)

                } else {
                    handleAuthError("No se pudo extraer el Steam ID.")
                }

            } else {
                val error = uri.getQueryParameter("openid.error")
                handleAuthError(error ?: "La autenticación con Steam fue cancelada o falló.")
            }

        } else {
            val receivedUrl = uri?.toString() ?: "ninguna"
            handleAuthError("Respuesta de autenticación inválida. URL recibida: $receivedUrl")
        }

        finish()
    }

    private fun extractSteamIdFromUrl(claimedId: String): String? {
        val pattern = Pattern.compile("https://steamcommunity.com/openid/id/(\\d+)")
        val matcher = pattern.matcher(claimedId)
        return if (matcher.find()) {
            matcher.group(1)
        } else {
            null
        }
    }

    private fun handleAuthError(message: String) {
        Log.e("SteamAuth", message)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


}
