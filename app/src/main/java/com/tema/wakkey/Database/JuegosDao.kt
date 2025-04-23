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
    suspend fun insertJuego(juego: JuegoEntity)

    @Query("SELECT * FROM juegos")
    suspend fun getAllJuegos(): List<JuegoEntity>

    @Query("SELECT * FROM juegos WHERE id = :id")
    suspend fun getJuegoById(id: Int): JuegoEntity?

    @Delete
    suspend fun deleteJuego(juego: JuegoEntity)

    @Update
    suspend fun updateJuego(juego: JuegoEntity)
}
