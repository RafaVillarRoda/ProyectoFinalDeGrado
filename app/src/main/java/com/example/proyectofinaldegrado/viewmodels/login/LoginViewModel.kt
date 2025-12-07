package com.example.proyectofinaldegrado.viewmodels.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinaldegrado.data.local.entity.User
import com.example.proyectofinaldegrado.data.repository.UserRepository

import kotlinx.coroutines.launch
import java.security.MessageDigest



class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    // --- Navegación para el registro ---
    private val _navigateToRegister = MutableLiveData<Boolean>()
    val navigateToRegister: LiveData<Boolean>
        get() = _navigateToRegister


    // LiveData para navegar a la pantalla principal tras un login exitoso
    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean>
        get() = _navigateToHome

    // LiveData para mostrar un mensaje de error
    private val _showError = MutableLiveData<String?>()
    val showError: LiveData<String?>
        get() = _showError

    private val _loggedInUser = MutableLiveData<User?>()
    val loggedInUser: LiveData<User?>
        get() = _loggedInUser


    // --- Lógica de la Vista ---

    fun onRegisterClicked() {
        _navigateToRegister.value = true
    }

    /**
     * Esta función es llamada para encriptar la contraseña utilizando SHA-256.
     */
    fun String.toSha256(): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(this.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Esta función es llamada desde la Vista cuando el usuario pulsa el botón de Login.
     */
    fun onLoginClicked(user: String, pass: String) {
        // 1. Validación básica en el ViewModel
        if (user.isBlank() || pass.isBlank()) {
            _showError.value = "El usuario y la contraseña no pueden estar vacíos"

            return
        }

        // Usamos viewModelScope para llamar a la función suspend del repositorio
        viewModelScope.launch {
            val foundUser = userRepository.findUserByUsername(user)
            val inputPasswordHash = pass.toSha256()

            //Comprobamos el resultado de la base de datos
            if (foundUser == null) {
                _showError.value = "Usuario no encontrado"

            } else if(foundUser.passwordHash == inputPasswordHash) {
                _loggedInUser.value = foundUser

                _navigateToHome.value = true

            } else {
                _showError.value = "Contraseña incorrecta"
            }
        }
    }



    // --- Funciones para gestionar los eventos de navegación y errores ---

    /**
     * Se llama desde la Vista después de haber navegado al registro.
     */
    fun onRegisterNavigationDone() {
        _navigateToRegister.value = false
    }

    /**
     * Se llama desde la Vista después de haber navegado a la pantalla principal.
     */
    fun onHomeNavigationDone() {
        _navigateToHome.value = false
    }

    /**
     * Se llama desde la Vista después de haber mostrado el mensaje de error.
     */
    fun onErrorShown() {
        _showError.value = null
    }
}