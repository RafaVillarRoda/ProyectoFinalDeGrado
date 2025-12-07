package com.example.proyectofinaldegrado.viewmodels.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyectofinaldegrado.data.repository.UserRepository

/**
 * Factory para crear instancias de LoginViewModel.
 * Es necesaria porque LoginViewModel ahora tiene un constructor con argumentos (el repositorio).
 */
class LoginViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}