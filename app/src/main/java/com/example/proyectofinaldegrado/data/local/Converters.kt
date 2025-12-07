package com.example.proyectofinaldegrado.data.local

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
class Converters {
    /**
     * Convierte un String (almacenado en la base de datos) de nuevo a un Instant.
     * Room llamará a esta función cuando lea datos de la base de datos.
     * @param value El String en formato ISO-8601, ej: "2025-11-21T21:30:00Z"
     * @return El objeto Instant correspondiente, o null si el valor era nulo.
     */
    @TypeConverter
    fun fromTimestamp(value: String?): Instant? {
        // El método 'parse' de Instant entiende el formato ISO-8601 por defecto.
        return value?.let { Instant.parse(it) }
    }

    /**
     * Convierte un objeto Instant a su representación como String para ser almacenado.
     * Room llamará a esta función cuando escriba datos en la base de datos.
     * @param instant El objeto Instant a convertir.
     * @return El String en formato ISO-8601, o null si el Instant era nulo.
     */
    @TypeConverter
    fun dateToTimestamp(instant: Instant?): String? {
        // El método 'toString()' de Instant ya devuelve el formato ISO-8601.
        return instant?.toString()
    }
}