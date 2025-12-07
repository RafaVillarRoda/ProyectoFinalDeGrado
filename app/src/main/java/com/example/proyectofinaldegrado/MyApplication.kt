package com.example.proyectofinaldegrado

import android.app.Application
import android.util.Log
import com.example.proyectofinaldegrado.data.local.AppDatabase
import com.example.proyectofinaldegrado.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MyApplication : Application() {


    val applicationScope = CoroutineScope(SupervisorJob())


    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this, applicationScope)
    }

    val userRepository: UserRepository by lazy {
        UserRepository(database.userDao(), database.bookDao(), database.filmDao(), database.serieDao())
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("SessionDebug", "MyApplication.onCreate: Inicializando SessionManager.")
        SessionManager.init(this)


        database
    }
}
    