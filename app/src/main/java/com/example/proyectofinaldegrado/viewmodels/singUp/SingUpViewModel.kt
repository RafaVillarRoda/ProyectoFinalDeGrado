package com.example.proyectofinaldegrado.viewmodels.singUp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinaldegrado.data.local.entity.User
import com.example.proyectofinaldegrado.data.repository.UserRepository
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.security.MessageDigest




class SingUpViewModel(private val userRepository: UserRepository) : ViewModel() {


    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean>
        get() = _navigateToLogin

    private val _showError = MutableLiveData<String?>()
    val showError: LiveData<String?>
        get() = _showError

    fun String.toSha256(): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(this.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun onSignUpClicked(userName: String, pass: String, confirmPass: String) {

        if (userName.isBlank() || pass.isBlank()) {
            _showError.value = "El usuario y la contraseña no pueden estar vacíos"
            return
        }
        if (!pass.equals(confirmPass)) {
            _showError.value = "Las contraseñas no coinciden"
            return
        }

        viewModelScope.launch {
            val existingUser = userRepository.findUserByUsername(userName)

            if (existingUser == null) {

                val newUser = User(userName = userName, passwordHash = pass.toSha256(), startDate = Clock.System.now())

                userRepository.registerUser(newUser)
                _navigateToLogin.value = true // Navegamos al login tras el éxito
                Log.d("SingUpViewModel", "Nuevo usuario registrado: $newUser")

            } else {

                _showError.value = "El nombre de usuario ya está en uso"
            }
        }
    }

    // --- Funciones de navegación y errores ---
    fun onLoginNavigationDone() {
        _navigateToLogin.value = false
    }

    fun onErrorShown() {
        _showError.value = null
    }
}
