package com.tema.wakkey.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface JuegoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJuego(juego: JuegoEntity) // Devuelve el ID del juego insertado

    @Query("SELECT * FROM juegos")
    suspend fun getAllJuegos(): List<JuegoEntity> // Devuelve todos los juegos

    @Query("SELECT * FROM juegos WHERE id = :id")
    suspend fun getJuegoById(id: Int): JuegoEntity?  // Devuelve un juego por su ID

    @Query("DELETE FROM juegos")
    suspend fun deleteAllJuegos() // Elimina todos los juegos

    @Delete
    suspend fun deleteJuego(juego: JuegoEntity) // Elimina un juego

    @Update
    suspend fun updateJuego(juego: JuegoEntity) // Actualiza un juego
}
