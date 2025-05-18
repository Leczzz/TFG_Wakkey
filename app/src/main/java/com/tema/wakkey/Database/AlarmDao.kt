package com.tema.wakkey.Database

import androidx.room.*

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity): Long // Devuelve el ID de la alarma insertada

    @Query("SELECT * FROM alarms")
    suspend fun getAllAlarms(): List<AlarmEntity> // Devuelve todas las alarmas

    @Delete
    suspend fun deleteAlarm(alarm: AlarmEntity) // Elimina una alarma

    @Query("SELECT dificultad FROM alarms WHERE id = :alarmaId LIMIT 1") // Devuelve la dificultad de una alarma
    suspend fun getDificultad(alarmaId: Int): String

    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)     // Actualiza una alarma

    @Query("SELECT * FROM alarms ORDER BY hora ASC")
    suspend fun getAllAlarmsOrderedByHour(): List<AlarmEntity> // Devuelve todas las alarmas ordenadas por hora

    @Query("DELETE FROM alarms")
    suspend fun deleteAll() // Elimina todas las alarmas

    @Query("SELECT * FROM alarms WHERE id = :id")
    suspend fun obtenerAlarmaPorId(id: Int): AlarmEntity? // Obtiene una alarma por su ID

}
