package com.solvetoscroll.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.solvetoscroll.data.entities.AttemptRecord

@Dao
interface AttemptDao {
    
    @Query("SELECT * FROM attempt_records WHERE packageName = :packageName")
    suspend fun getAttemptRecord(packageName: String): AttemptRecord?
    
    @Query("SELECT attemptCount FROM attempt_records WHERE packageName = :packageName")
    suspend fun getAttemptCount(packageName: String): Int?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attemptRecord: AttemptRecord)
    
    @Update
    suspend fun update(attemptRecord: AttemptRecord)
    
    @Query("UPDATE attempt_records SET attemptCount = attemptCount + 1, lastAttemptTime = :timestamp WHERE packageName = :packageName")
    suspend fun incrementAttempt(packageName: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE attempt_records SET attemptCount = 0, lastSuccessTime = :timestamp WHERE packageName = :packageName")
    suspend fun resetAttempts(packageName: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("DELETE FROM attempt_records WHERE packageName = :packageName")
    suspend fun delete(packageName: String)
    
    @Query("DELETE FROM attempt_records WHERE lastAttemptTime < :threshold")
    suspend fun deleteOldRecords(threshold: Long)
}
