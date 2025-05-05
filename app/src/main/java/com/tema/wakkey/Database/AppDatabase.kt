package com.tema.wakkey.Database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context


@Database(entities = [AlarmEntity::class, JuegoEntity::class], version = 1)
    abstract class AppDatabase : RoomDatabase() {
        abstract fun alarmDao(): AlarmDao
        abstract fun juegoDao(): JuegoDao

        companion object {
            @Volatile
            private var INSTANCE: AppDatabase? = null

            fun getInstance(context: Context): AppDatabase {
                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "wakkey_database"
                    ).build()
                    INSTANCE = instance
                    instance
                }
            }
        }
    }
