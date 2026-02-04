package com.solvetoscroll.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.solvetoscroll.data.entities.BlockedApp
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedAppDao {
    
    @Query("SELECT * FROM blocked_apps ORDER BY appName ASC")
    fun getAllBlockedApps(): Flow<List<BlockedApp>>
    
    @Query("SELECT * FROM blocked_apps WHERE isEnabled = 1 ORDER BY appName ASC")
    fun getEnabledBlockedApps(): Flow<List<BlockedApp>>
    
    @Query("SELECT * FROM blocked_apps WHERE packageName = :packageName")
    suspend fun getBlockedApp(packageName: String): BlockedApp?
    
    @Query("SELECT EXISTS(SELECT 1 FROM blocked_apps WHERE packageName = :packageName AND isEnabled = 1)")
    suspend fun isAppBlocked(packageName: String): Boolean
    
    @Query("SELECT COUNT(*) FROM blocked_apps WHERE isEnabled = 1")
    fun getEnabledBlockedAppsCount(): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(blockedApp: BlockedApp)
    
    @Update
    suspend fun update(blockedApp: BlockedApp)
    
    @Delete
    suspend fun delete(blockedApp: BlockedApp)
    
    @Query("DELETE FROM blocked_apps WHERE packageName = :packageName")
    suspend fun deleteByPackageName(packageName: String)
    
    @Query("UPDATE blocked_apps SET isEnabled = :isEnabled WHERE packageName = :packageName")
    suspend fun setEnabled(packageName: String, isEnabled: Boolean)
}
