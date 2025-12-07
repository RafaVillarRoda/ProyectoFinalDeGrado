package com.example.proyectofinaldegrado.ui

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.proyectofinaldegrado.R
import com.example.proyectofinaldegrado.data.local.AppDatabase
import com.example.proyectofinaldegrado.data.repository.UserRepository
import com.example.proyectofinaldegrado.viewmodels.login.LoginViewModel
import com.example.proyectofinaldegrado.viewmodels.login.LoginViewModelFactory
import com.example.proyectofinaldegrado.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope.coroutineContext


class Login : AppCompatActivity() {

    // 1. Inyectamos el ViewModel usando la Factory. Esta es la forma correcta.
    @OptIn(DelicateCoroutinesApi::class)
    private val loginViewModel: LoginViewModel by viewModels {

        val database = AppDatabase.getDatabase(applicationContext, CoroutineScope(coroutineContext))
        val repository = UserRepository(database.userDao(), database.bookDao(), database.filmDao(), database.serieDao())
        LoginViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configura los listeners y observadores
        setupClickListeners()
        setupObservers()
    }

    private fun setupClickListeners() {
        // Configura el enlace de registro
        setupRegisterLink()

        // Configura el botón de login
        val loginButton = findViewById<Button>(R.id.LoginBtn)
        val userInput = findViewById<EditText>(R.id.UserTv)
        val passInput = findViewById<EditText>(R.id.PasswordTv)

        loginButton.setOnClickListener {
            val user = userInput.text.toString()
            val pass = passInput.text.toString()
            loginViewModel.onLoginClicked(user, pass)
        }

    }

    private fun setupObservers() {
        // Observador para navegar al registro
        loginViewModel.navigateToRegister.observe(this, Observer { shouldNavigate ->
            if (shouldNavigate == true) {
                startActivity(Intent(this, SingUp::class.java))
                loginViewModel.onRegisterNavigationDone()
            }
        })

        // Observador para navegar a la pantalla principal (Home)
        loginViewModel.navigateToHome.observe(this, Observer { shouldNavigate ->
            if (shouldNavigate == true) {
                Toast.makeText(this, "Login Correcto!", Toast.LENGTH_SHORT).show()

                val userObject = loginViewModel.loggedInUser.value
                if (userObject != null) {
                    Log.d("SessionDebug", "Login.kt: Llamando a SessionManager.login() con el usuario: $userObject")

                    SessionManager.login(userObject)
                }else{
                    Log.d("SessionDebug", "Login.kt: No se encontró el usuario en el ViewModel")
                }
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                loginViewModel.onHomeNavigationDone()
            }
        })

        // Observador para mostrar errores
        loginViewModel.showError.observe(this, Observer { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                loginViewModel.onErrorShown()
            }
        })
    }

    private fun setupRegisterLink() {
        val newToPlatformTextView = findViewById<TextView>(R.id.NewToPlatformTv)
        val fullText = getString(R.string.new_to_platform)
        val registerText = "Registrate"
        val spannableString = SpannableString(fullText)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                loginViewModel.onRegisterClicked()
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }

        val startIndex = fullText.indexOf(registerText)
        if (startIndex != -1) {
            spannableString.setSpan(clickableSpan, startIndex, startIndex + registerText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        newToPlatformTextView.text = spannableString
        newToPlatformTextView.movementMethod = LinkMovementMethod.getInstance()
    }
}