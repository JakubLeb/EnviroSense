package com.example.envirosense.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Główna baza danych Room aplikacji
 */
@Database(
    entities = [MeasurementEntity::class],
    version = 1,
    exportSchema = false
)
abstract class EnviroDatabase : RoomDatabase() {
    
    abstract fun measurementDao(): MeasurementDao
    
    companion object {
        private const val DATABASE_NAME = "enviro_database"
        
        @Volatile
        private var INSTANCE: EnviroDatabase? = null
        
        /**
         * Zwraca singleton instancję bazy danych
         */
        fun getInstance(context: Context): EnviroDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EnviroDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
