package com.example.proyectofinaldegrado.data.repository

import com.example.proyectofinaldegrado.data.local.dao.BookDao
import com.example.proyectofinaldegrado.data.local.dao.FilmDao
import com.example.proyectofinaldegrado.data.local.dao.SerieDao
import com.example.proyectofinaldegrado.data.local.dao.SteamGamesDao
import com.example.proyectofinaldegrado.data.local.dao.UserDao
import com.example.proyectofinaldegrado.data.local.entity.Book
import com.example.proyectofinaldegrado.data.local.entity.Film
import com.example.proyectofinaldegrado.data.local.entity.Serie
import com.example.proyectofinaldegrado.data.local.entity.Game
import com.example.proyectofinaldegrado.data.local.entity.User
import com.example.proyectofinaldegrado.data.local.entity.UserFullLibrary
import com.example.proyectofinaldegrado.data.local.entity.UserLibraryItem
import kotlinx.datetime.Clock
import kotlin.collections.emptyList

/**
 * El Repositorio es el único intermediario entre los ViewModels y las fuentes de datos.
 */
class UserRepository(
    private val userDao: UserDao,
    private val bookDao: BookDao,
    private val filmDao: FilmDao,
    private val serieDao: SerieDao,
    private val steamGamesDao: SteamGamesDao
) {

    /**
     * Busca un usuario por su nombre de usuario.
     * @param userName El nombre de usuario a buscar.
     * @return El usuario si se encuentra, o null.
     */
    suspend fun findUserByUsername(userName: String): User? {
        return userDao.getUserByUsername(userName)
    }

    /**
     * Inserta un nuevo usuario.
     * Aquí se podría añadir la lógica para hashear la contraseña antes de guardarla.
     * @param user El usuario a insertar.
     */
    suspend fun registerUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun getFullLibraryForUser(userName: String): UserFullLibrary? {
        val user = userDao.getUserByUsername(userName) ?: return null

        val bookIds = userDao.getBookIdsForUser(userName)
        val filmIds = userDao.getFilmIdsForUser(userName)
        val serieIds = userDao.getSerieIdsForUser(userName)
        val gamesIds = userDao.getSteamGameIdsForUser(userName)


        val books = if (bookIds.isNotEmpty()) bookDao.getBookByTitle(bookIds) else emptyList()
        val films = if (filmIds.isNotEmpty()) filmDao.getFilmByTitle(filmIds) else emptyList()
        val series =
            if (serieIds.isNotEmpty()) serieDao.getSerieBySerieTitle(serieIds) else emptyList()


        val libraryItems = userDao.getLibraryItemsForUser(userName)

        val steamGameNames = libraryItems
            .filter { it.itemType == "game" }
            .map { it.itemId }

        //Cambiar esto
        val games = if (steamGameNames.isNotEmpty()) {
            steamGamesDao.getGamesByNames(steamGameNames)
        } else {
            emptyList()
        }

        return UserFullLibrary(
            user = user,
            books = books,
            films = films,
            series = series,
            games = games,
            libraryItems = libraryItems
        )

    }

    suspend fun addItemToUserLibrary(item: UserLibraryItem) {

        userDao.insertLibraryItem(item)
    }

    suspend fun getBooksByTitle(title: String): List<Book> {
        return bookDao.getBookBySingleTitle(title)
    }

    suspend fun getItemName(title: String, user: String): UserLibraryItem? {
        return userDao.getLibraryItemTitle(user, title)

    }

    suspend fun getAllFilms(): List<Film> {
        return filmDao.getAllFilms()
    }

    suspend fun getAllSeries(): List<Serie> {
        return serieDao.getAllSeries()
    }

    suspend fun getAllLibraryItems(): List<UserLibraryItem> {
        return userDao.getAllLibraryItems()
    }


    suspend fun deleteItem(user: String, itemId: String) {
        userDao.deleteLibraryItem(user, itemId)
    }

    suspend fun insertSteamGames(games: List<Game>) {
        steamGamesDao.upsertSteamGame(games)


        games.forEach { game ->
            val libraryItem = UserLibraryItem(
                userName = game.userName,
                itemId = game.name,
                itemType = "game",
                rating = 0,
                additionDate = Clock.System.now()
            )

            if (userDao.getLibraryItemTitle(game.userName, libraryItem.itemId) == null) {
                userDao.insertLibraryItem(libraryItem)
            }
        }
    }

    suspend fun updateSteamId(userName: String, steamId: String) {
        userDao.updateSteamId(userName, steamId)
    }


    suspend fun deleteGamesByUser(userName: String) {
        steamGamesDao.deleteAllSteamGames(userName)

    }
}