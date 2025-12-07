package com.example.proyectofinaldegrado.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectofinaldegrado.data.local.entity.Book

@Dao
interface BookDao {
    /**
     * Inserta un libro en la tabla. Si el libro ya existe, no hace nada.
     * @param book El libro a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBook(book: Book)


    @Query("SELECT * FROM books")
    suspend fun getAllBooks(): List<Book>

    @Query("SELECT * FROM books WHERE book_title LIKE '%'|| :bookTitle ||'%'")
    suspend fun getBookBySingleTitle(bookTitle: String): List<Book>


    @Query("SELECT * FROM books WHERE book_title IN (:bookTitle) ")
    suspend fun getBookByTitle(bookTitle: List<String>): List<Book>





}

