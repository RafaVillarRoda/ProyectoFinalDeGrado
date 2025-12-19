package com.example.proyectofinaldegrado

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.proyectofinaldegrado.data.local.entity.User
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

object SessionManager {

    private const val PREFS_NAME = "AppSession"
    private const val KEY_USERNAME = "username"

    private const val KEY_REGISTRATION_DATE = "registration_date"
    private var sharedPreferences: SharedPreferences? = null

    var currentUser: User? = null

    fun init(context: Context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

            val username = sharedPreferences?.getString(KEY_USERNAME, null)
            if (username != null) {
                val registrationDateMillis =
                    sharedPreferences?.getLong(KEY_REGISTRATION_DATE, -1L) ?: -1L
                val registrationDate = if (registrationDateMillis != -1L) {
                    Instant.fromEpochMilliseconds(registrationDateMillis)
                } else {
                    Clock.System.now()
                }
                currentUser =
                    User(userName = username, passwordHash = "", startDate = registrationDate)
            }
            Log.d(
                "SessionDebug",
                "SessionManager.init: Usuario cargado de SharedPreferences -> ${currentUser}"
            )
        }
    }

    fun login(user: User) {
        currentUser = user
        Log.d("SessionDebug", "SessionManager.login: Guardando usuario -> '$user'")
        sharedPreferences?.edit()?.apply {
            putString(KEY_USERNAME, user.userName)
            putLong(KEY_REGISTRATION_DATE, user.startDate.toEpochMilliseconds())
            apply()
            Log.d("SessionDebug", "SessionManager.login: SharedPreferences actualizado.")
        } ?: Log.e("SessionDebug", "SessionManager.login: ¡ERROR! SharedPreferences es null.")
    }

    fun logout() {
        Log.d("SessionDebug", "SessionManager.logout: Cerrando sesión del usuario '${currentUser}'")
        currentUser = null
        sharedPreferences?.edit()?.apply {
            android.system.Os.remove(KEY_USERNAME)
            android.system.Os.remove(KEY_REGISTRATION_DATE)
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return currentUser != null
    }
}