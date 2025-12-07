package com.example.proyectofinaldegrado.viewmodels.singUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyectofinaldegrado.data.repository.UserRepository

class SingUpViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SingUpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SingUpViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}