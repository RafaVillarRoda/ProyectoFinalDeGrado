package com.example.proyectofinaldegrado.viewmodels.mainActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinaldegrado.SessionManager
import com.example.proyectofinaldegrado.data.local.entity.Book
import com.example.proyectofinaldegrado.data.local.entity.Film
import com.example.proyectofinaldegrado.data.local.entity.Serie
import com.example.proyectofinaldegrado.data.local.entity.UserFullLibrary
import com.example.proyectofinaldegrado.data.local.entity.UserLibraryItem
import com.example.proyectofinaldegrado.data.repository.UserRepository
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
        return  runBlocking {
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

}

