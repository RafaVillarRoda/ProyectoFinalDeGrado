package com.example.proyectofinaldegrado.viewmodels.mainActivity

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinaldegrado.SessionManager
import com.example.proyectofinaldegrado.data.local.entity.Book
import com.example.proyectofinaldegrado.data.local.entity.Film
import com.example.proyectofinaldegrado.data.local.entity.Game
import com.example.proyectofinaldegrado.data.local.entity.MediaItem
import com.example.proyectofinaldegrado.data.local.entity.Serie
import com.example.proyectofinaldegrado.data.local.entity.UserFullLibrary
import com.example.proyectofinaldegrado.data.local.entity.UserLibraryItem
import com.example.proyectofinaldegrado.data.repository.UserRepository
import com.example.proyectofinaldegrado.ui.steam.SteamGame
import com.example.proyectofinaldegrado.ui.steam.SteamRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {


    private val _userLibrary = MutableStateFlow<UserFullLibrary?>(null)
    val userLibrary: StateFlow<UserFullLibrary?> = _userLibrary.asStateFlow()


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {

        loadUserLibrary()
    }

    fun loadUserLibrary() {

        SessionManager.currentUser?.let { user ->
            viewModelScope.launch {
                _isLoading.value = true
                _userLibrary.value = userRepository.getFullLibraryForUser(user.userName)
                _isLoading.value = false
            }
        }
    }

    fun addBookToLibrary(book: Book, userRating: Int) {
        SessionManager.currentUser?.let { user ->
            viewModelScope.launch {
                val libraryItem = UserLibraryItem(
                    userName = user.userName,
                    itemId = book.bookTitle,
                    itemType = "book",
                    rating = userRating,
                    additionDate = Clock.System.now()
                )
                userRepository.addItemToUserLibrary(libraryItem)

                loadUserLibrary()
            }
        }
    }

    fun addFilmToLibrary(film: Film, userRating: Int) {
        SessionManager.currentUser?.let { user ->
            viewModelScope.launch {
                val libraryItem = UserLibraryItem(
                    userName = user.userName,
                    itemId = film.filmTitle,
                    itemType = "film",
                    rating = userRating,
                    additionDate = Clock.System.now()
                )
                userRepository.addItemToUserLibrary(libraryItem)

                loadUserLibrary()
            }
        }
    }

    fun addSerieToLibrary(serie: Serie, userRating: Int) {
        SessionManager.currentUser?.let { user ->
            viewModelScope.launch {
                val libraryItem = UserLibraryItem(
                    userName = user.userName,
                    itemId = serie.serieTitle,
                    itemType = "serie",
                    rating = userRating,
                    additionDate = Clock.System.now()
                )
                userRepository.addItemToUserLibrary(libraryItem)

                loadUserLibrary()
            }
        }
    }

    fun getAllBooks(title: String): List<Book> {
        return runBlocking {
            userRepository.getBooksByTitle(title)
        }
    }

    fun getItemName(title: String): UserLibraryItem? {
        return runBlocking {
            userRepository.getItemName(title, SessionManager.currentUser!!.userName)
        }
    }

    fun getAllFilms(title: String): List<Film> {
        return runBlocking {
            userRepository.getAllFilms()
        }
    }

    fun getAllSeries(title: String): List<Serie> {
        return runBlocking {
            userRepository.getAllSeries()
        }
    }

    fun getAllLibraryItems(): List<UserLibraryItem> {
        return runBlocking {
            userRepository.getAllLibraryItems()
        }
    }

    fun deleteItem(item: MediaItem) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = SessionManager.currentUser
            if (user != null) {
                userRepository.deleteItem(user.userName, item.title)
                loadUserLibrary()
            }
        }
    }

    fun updateSteamId(steamId: String) {
        viewModelScope.launch {
            val user = SessionManager.currentUser
            if (user != null) {
                userRepository.updateSteamId(user.userName, steamId)
                SessionManager.currentUser = user.copy(steamID = steamId)

                loadUserLibrary()
            }
        }
    }

    var steamGames by mutableStateOf<List<SteamGame>>(emptyList())
    private fun loadSteamLibrary(steamId: String, userApiKey: String) {
        viewModelScope.launch {
            val games = SteamRepository.getOwnedGames(steamId, userApiKey)
            steamGames = games
        }
    }

    var isSteamLoading by mutableStateOf(false)
        private set

    fun syncSteamLibrary(steamId: String?, userApiKey: String) {
        viewModelScope.launch {
            try {
                isSteamLoading = true

                // Usamos el ID que viene o el de la sesión
                val idToUse = steamId ?: SessionManager.currentUser?.steamID

                Log.d("MainViewModel", "Iniciando sincronización. API Key: ${userApiKey.take(4)}... ID: $idToUse")

                if (idToUse.isNullOrBlank()) {
                    Log.e("MainViewModel", "Error: No hay Steam ID disponible.")
                    return@launch
                }

                // LLAMADA A RED
                val games = SteamRepository.getOwnedGames(idToUse, userApiKey)

                Log.d("MainViewModel", "Respuesta de Steam: ${games.size} juegos encontrados.")

                val user = SessionManager.currentUser
                if (user != null && games.isNotEmpty()) {
                    val steamGameEntities = games.map { game ->
                        Game(
                            appid = game.appid,
                            name = game.name ?: "Unknown Game",
                            userName = user.userName,
                            playtimeForever = game.playtime_forever/60,
                            imgIconUrl = game.img_icon_url
                        )
                    }

                    // Operaciones en Base de Datos
                    userRepository.deleteGamesByUser(user.userName)
                    userRepository.insertSteamGames(steamGameEntities)

                    // Actualizar la librería local (StateFlow)
                    loadUserLibrary()

                    Log.d("MainViewModel", "Sincronización completada y guardada en DB.")
                } else {
                    if (games.isEmpty()) {
                        Log.w("MainViewModel", "Steam no devolvió juegos. Esto ocurre si la API Key es inválida o el perfil sigue detectándose como privado por Steam.")
                    }
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "ERROR CRÍTICO: ${e.message}")
                e.printStackTrace()
            } finally {
                isSteamLoading = false
            }
        }
    }
    fun editNickname(newNickname: String) {
        viewModelScope.launch {
            val user = SessionManager.currentUser
            if (user != null) {
                userRepository.editNickname(user.userName, newNickname)

            }
        }
    }
}

