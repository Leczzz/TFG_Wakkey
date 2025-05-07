package com.tema.wakkey.Database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AlarmEntity::class, JuegoEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {

    // DAO para Alarmas
    abstract fun alarmDao(): AlarmDao

    // DAO para Juegos
    abstract fun juegoDao(): JuegoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // MEtodo para obtener la instancia de la base de datos
        fun getInstance(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wakkey_database"  // El nombre de la base de datos
                ).fallbackToDestructiveMigration() //
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
