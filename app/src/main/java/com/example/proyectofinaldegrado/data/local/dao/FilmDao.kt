package com.example.proyectofinaldegrado.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectofinaldegrado.data.local.entity.Film

@Dao
interface FilmDao {
    /**
     * Inserta una pelicula en la tabla. Si la pelicula ya existe, no hace nada.
     * @param film La pelicula a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFilm(film: Film)

    /**
     * Busca una pelicula por su titulo.
     * @param filmTitle El nombre de la pelicula a buscar.
     * @return El objeto Film si se encuentra, o null si no.
     */
    @Query("SELECT * FROM films WHERE film_title IN (:filmTitle) ")
    suspend fun getFilmByTitle(filmTitle: List<String>): List<Film>

    @Query("SELECT * FROM films")
    suspend fun getAllFilms(): List<Film>


}