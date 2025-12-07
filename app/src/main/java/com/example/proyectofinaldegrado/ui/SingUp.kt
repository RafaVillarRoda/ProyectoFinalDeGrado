package com.example.proyectofinaldegrado.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.proyectofinaldegrado.R
import com.example.proyectofinaldegrado.data.local.AppDatabase
import com.example.proyectofinaldegrado.data.repository.UserRepository
import com.example.proyectofinaldegrado.viewmodels.singUp.SingUpViewModelFactory
import com.example.proyectofinaldegrado.viewmodels.singUp.SingUpViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope.coroutineContext

class SingUp : AppCompatActivity() {

    // 1. El ViewModel se declara DENTRO de la clase de la Activity
    @OptIn(DelicateCoroutinesApi::class)
    private val signUpViewModel: SingUpViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext, CoroutineScope(coroutineContext))
        val repository = UserRepository(database.userDao(), database.bookDao(), database.filmDao(), database.serieDao())
        SingUpViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sing_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 2. Llamamos a las funciones de configuración
        setupClickListeners()
        setupObservers()
    }

    private fun setupClickListeners() {
        val signUpButton = findViewById<Button>(R.id.SingUpBtn)
        val userInput = findViewById<EditText>(R.id.UserSingUpEt)
        val passInput = findViewById<EditText>(R.id.PasswordSingUpEt)
        val passConfirmationInput = findViewById<EditText>(R.id.PasswordSingUpConfirmationEt)

        signUpButton.setOnClickListener {
            val user = userInput.text.toString()
            val pass = passInput.text.toString()
            val passConfirmation = passConfirmationInput.text.toString()
            

            signUpViewModel.onSignUpClicked(user,pass, passConfirmation)
        }
    }

    private fun setupObservers() {
        // 3. Observador para navegar de vuelta al Login
        signUpViewModel.navigateToLogin.observe(this, Observer { shouldNavigate ->
            if (shouldNavigate == true) {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                signUpViewModel.onLoginNavigationDone()
                finish() // Cierra la actividad de SignUp y vuelve a Login
            }
        })

        // 4. Observador para mostrar errores
        signUpViewModel.showError.observe(this, Observer { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                signUpViewModel.onErrorShown()
            }
        })
    }
}