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
        private var INSTANCE: AppDatabase? = null // Instancia de la base de datos

        // Metodo para obtener la instancia de la base de datos
        fun getInstance(context: android.content.Context): AppDatabase { // Contexto de la aplicación
            return INSTANCE ?: synchronized(this) { // Bloquea la creación de múltiples instancias
                val instance = Room.databaseBuilder( // Construye la base de datos
                    context.applicationContext, // Contexto de la aplicación
                    AppDatabase::class.java, // Clase de la base de datos
                    "wakkey_database"  // El nombre de la base de datos
                ).fallbackToDestructiveMigration() // Permite migraciones destructivas
                 .build() // Construye la base de datos
                INSTANCE = instance // Guarda la instancia
                instance // Devuelve la instancia
            }
        }
    }
}
