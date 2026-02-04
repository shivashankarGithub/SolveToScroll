package com.solvetoscroll.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.solvetoscroll.data.entities.Schedule
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    
    @Query("SELECT * FROM schedules WHERE packageName = :packageName AND isEnabled = 1")
    fun getSchedulesForApp(packageName: String): Flow<List<Schedule>>
    
    @Query("SELECT * FROM schedules WHERE packageName = :packageName AND isEnabled = 1")
    suspend fun getSchedulesForAppSync(packageName: String): List<Schedule>
    
    @Query("SELECT * FROM schedules WHERE packageName = :packageName")
    suspend fun getAllSchedulesForApp(packageName: String): List<Schedule>
    
    @Query("SELECT * FROM schedules WHERE id = :id")
    suspend fun getSchedule(id: Long): Schedule?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: Schedule): Long
    
    @Update
    suspend fun update(schedule: Schedule)
    
    @Delete
    suspend fun delete(schedule: Schedule)
    
    @Query("DELETE FROM schedules WHERE packageName = :packageName")
    suspend fun deleteAllForApp(packageName: String)
    
    @Query("UPDATE schedules SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun setEnabled(id: Long, isEnabled: Boolean)
}
