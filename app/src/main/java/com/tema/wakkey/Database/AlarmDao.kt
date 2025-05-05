package com.tema.wakkey.Database

import androidx.room.*

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity)

    @Query("SELECT * FROM alarms")
    suspend fun getAllAlarms(): List<AlarmEntity>

    @Delete
    suspend fun deleteAlarm(alarm: AlarmEntity)

    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)
<<<<<<< Updated upstream
=======

    @Query("SELECT * FROM alarms ORDER BY hora ASC")
    suspend fun getAllAlarmsOrderedByHour(): List<AlarmEntity>

    @Query("DELETE FROM alarms")
    suspend fun deleteAll()

    @Query("SELECT * FROM alarms WHERE id = :id")
    suspend fun obtenerAlarmaPorId(id: Int): AlarmEntity?

>>>>>>> Stashed changes
}
