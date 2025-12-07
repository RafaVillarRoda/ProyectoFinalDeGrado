package com.example.proyectofinaldegrado.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectofinaldegrado.data.local.entity.Film
import com.example.proyectofinaldegrado.data.local.entity.Serie

@Dao
interface SerieDao {
    /**
     * Inserta una serie en la tabla. Si la serie ya existe, no hace nada.
     * @param serie La serie a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSerie(serie: Serie)

    /**
     *
     * Busca una serie por su titulo.
     * @param serieTitle El titulo de la serie a buscar.
     * @return El objeto Serie si se encuentra, o null si no.
     */
    @Query("SELECT * FROM series WHERE serie_title IN (:serieTitle)")
    suspend fun getSerieBySerieTitle(serieTitle: List<String>): List<Serie>

    @Query("SELECT * FROM series")
    suspend fun getAllSeries(): List<Serie>
}