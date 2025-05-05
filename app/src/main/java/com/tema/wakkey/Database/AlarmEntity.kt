package com.tema.wakkey.Database


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val hora: String, // formato HH:mm
    val idJuego: Int,
    val dificultad: Char,
    val diasActivos: String, // Formato "LMXJVSD" para los días de la semana
    var esActivo: Boolean,
    val sonido: String
)

