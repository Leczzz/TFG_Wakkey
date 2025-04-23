package com.tema.wakkey.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "juegos")
data class JuegoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val tieneDificultad: Boolean,
    val imagenResId: Int
)
