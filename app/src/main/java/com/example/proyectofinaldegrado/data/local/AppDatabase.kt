package com.example.proyectofinaldegrado.data.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
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
import com.example.proyectofinaldegrado.data.local.entity.UserLibraryItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.InputStreamReader

@Database(entities = [User::class, Film::class, Serie::class, Book::class, UserLibraryItem::class, Game::class], version = 15, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun filmDao(): FilmDao
    abstract fun serieDao(): SerieDao
    abstract fun bookDao(): BookDao

    abstract fun steamGamesDao(): SteamGamesDao




    private class DatabaseCallback(
        private val scope: CoroutineScope,
        private val context: Context,
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
           super.onCreate(db)
            Log.d("DatabaseCallback", "onCreate: Base de datos creada. Lanzando pre-poblado.")

            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(context, database)
                }
            } ?: Log.e("DatabaseCallback", "onCreate: ¡ERROR CRÍTICO! La instancia de la BD es nula.")
        }

        private suspend fun populateDatabase(context: Context, database: AppDatabase) {
            Log.d("DatabaseCallback", "populateDatabase: Obteniendo DAOs y poblando tablas...")
            prePopulateFilms(context, database.filmDao())
            prePopulateSeries(context, database.serieDao())
            prePopulateBooks(context, database.bookDao())
            Log.d("DatabaseCallback", "populateDatabase: Pre-poblado finalizado.")
        }

        // El resto de funciones de pre-poblado se quedan igual, con logs
        private suspend fun prePopulateFilms(context: Context, filmDao: FilmDao) {
            try {
                context.assets.open("film.json").use { inputStream ->
                    InputStreamReader(inputStream).use { reader ->
                        val filmListType = object : TypeToken<List<Film>>() {}.type
                        val films: List<Film> = Gson().fromJson(reader, filmListType)
                        // RECOMENDACIÓN: Crea un método insertAll en tu DAO
                        films.forEach { filmDao.insertFilm(it) }
                        Log.d("DatabaseCallback", "prePopulateFilms: ${films.size} películas insertadas.")
                    }
                }
            } catch (e: Exception) {
                Log.e("DatabaseCallback", "Error al pre-poblar películas", e)
            }
        }

        private suspend fun prePopulateSeries(context: Context, serieDao: SerieDao) {
            try {
                context.assets.open("serie.json").use { inputStream ->
                    InputStreamReader(inputStream).use { reader ->
                        val serieListType = object : TypeToken<List<Serie>>() {}.type
                        val series: List<Serie> = Gson().fromJson(reader, serieListType)
                        series.forEach { serieDao.insertSerie(it) }
                        Log.d("DatabaseCallback", "prePopulateSeries: ${series.size} series insertadas.")
                    }
                }
            } catch (e: Exception) {
                Log.e("DatabaseCallback", "Error al pre-poblar series", e)
            }
        }

        private suspend fun prePopulateBooks(context: Context, bookDao: BookDao) {
            try {
                context.assets.open("book.json").use { inputStream ->
                    InputStreamReader(inputStream).use { reader ->
                        val bookListType = object : TypeToken<List<Book>>() {}.type
                        val books: List<Book> = Gson().fromJson(reader, bookListType)
                        books.forEach { bookDao.insertBook(it) }
                        Log.d("DatabaseCallback", "prePopulateBooks: ${books.size} libros insertados.")
                    }
                }
            } catch (e: Exception) {
                Log.e("DatabaseCallback", "Error al pre-poblar libros", e)
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null


        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addCallback(DatabaseCallback(scope, context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
