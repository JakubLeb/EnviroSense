package com.example.envirosense.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO dla operacji na pomiarach w bazie danych
 */
@Dao
interface MeasurementDao {
    
    /**
     * Pobiera wszystkie pomiary posortowane od najnowszego
     */
    @Query("SELECT * FROM measurements ORDER BY timestamp DESC")
    fun getAllMeasurements(): Flow<List<MeasurementEntity>>
    
    /**
     * Pobiera pojedynczy pomiar po ID
     */
    @Query("SELECT * FROM measurements WHERE id = :id")
    suspend fun getMeasurementById(id: Long): MeasurementEntity?
    
    /**
     * Pobiera pomiary z określonego zakresu dat
     */
    @Query("SELECT * FROM measurements WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getMeasurementsBetween(startTime: Long, endTime: Long): Flow<List<MeasurementEntity>>
    
    /**
     * Pobiera ostatnie N pomiarów
     */
    @Query("SELECT * FROM measurements ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentMeasurements(limit: Int): Flow<List<MeasurementEntity>>
    
    /**
     * Pobiera liczbę wszystkich pomiarów
     */
    @Query("SELECT COUNT(*) FROM measurements")
    suspend fun getMeasurementCount(): Int
    
    /**
     * Wstawia nowy pomiar
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: MeasurementEntity): Long
    
    /**
     * Usuwa pomiar
     */
    @Delete
    suspend fun deleteMeasurement(measurement: MeasurementEntity)
    
    /**
     * Usuwa pomiar po ID
     */
    @Query("DELETE FROM measurements WHERE id = :id")
    suspend fun deleteMeasurementById(id: Long)
    
    /**
     * Usuwa wszystkie pomiary
     */
    @Query("DELETE FROM measurements")
    suspend fun deleteAllMeasurements()
    
    /**
     * Pobiera średnią temperaturę z ostatnich N pomiarów
     */
    @Query("SELECT AVG(temperature) FROM measurements ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getAverageTemperature(limit: Int): Double?
    
    /**
     * Pobiera średnie AQI z ostatnich N pomiarów
     */
    @Query("SELECT AVG(aqi) FROM measurements ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getAverageAqi(limit: Int): Double?
}
