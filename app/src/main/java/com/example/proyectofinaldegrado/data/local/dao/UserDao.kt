package com.example.proyectofinaldegrado.data.local.dao

import android.content.ClipData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectofinaldegrado.data.local.entity.User
import com.example.proyectofinaldegrado.data.local.entity.UserLibraryItem

@Dao
interface UserDao {

    /**
     * Inserta un usuario en la tabla. Si el usuario ya existe, no hace nada.
     * @param user El usuario a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User)

    /**
     * Busca un usuario por su nombre de usuario.
     * @param userName El nombre de usuario a buscar.
     * @return El objeto User si se encuentra, o null si no.
     */
    @Query("SELECT * FROM users WHERE user_name = :userName LIMIT 1")
    suspend fun getUserByUsername(userName: String): User?

    @Query("SELECT itemId FROM user_library WHERE userName = :userName AND itemType = 'book'")
    suspend fun getBookIdsForUser(userName: String): List<String>

    @Query("SELECT itemId FROM user_library WHERE userName = :userName AND itemType = 'film'")
    suspend fun getFilmIdsForUser(userName: String): List<String>

    @Query("SELECT itemId FROM user_library WHERE userName = :userName AND itemType = 'serie'")
    suspend fun getSerieIdsForUser(userName: String): List<String>

    @Query("SELECT itemId FROM user_library WHERE userName = :userName AND itemType = 'steamGame'")
    suspend fun getSteamGameIdsForUser(userName: String): List<String>


    @Query("SELECT * FROM user_library WHERE userName = :userName AND itemId = :itemId")
    suspend fun getLibraryItemTitle(userName: String, itemId: String): UserLibraryItem?

    @Query("SELECT * FROM user_library WHERE userName = :userName")
    suspend fun getLibraryItemsForUser(userName: String): List<UserLibraryItem>

    @Query("SELECT * FROM user_library")
    suspend fun getAllLibraryItems(): List<UserLibraryItem>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLibraryItem(item: UserLibraryItem)

    @Query("DELETE FROM user_library WHERE itemId = :itemId AND userName = :userName")
    suspend fun deleteLibraryItem(userName: String, itemId: String)
   @Query("UPDATE users SET steamID = :steamID WHERE user_name = :userName")
    suspend fun updateSteamId(userName: String, steamID: String)

}
