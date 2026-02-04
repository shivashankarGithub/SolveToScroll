package com.solvetoscroll.scheduler

import com.solvetoscroll.data.dao.BlockedAppDao
import com.solvetoscroll.data.dao.ScheduleDao
import com.solvetoscroll.data.entities.BlockedApp
import com.solvetoscroll.data.entities.Schedule
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Manages blocked apps and their schedules.
 */
class ScheduleManager @Inject constructor(
    private val blockedAppDao: BlockedAppDao,
    private val scheduleDao: ScheduleDao
) {
    
    /**
     * Gets all blocked apps.
     */
    fun getAllBlockedApps(): Flow<List<BlockedApp>> {
        return blockedAppDao.getAllBlockedApps()
    }
    
    /**
     * Gets enabled blocked apps count.
     */
    fun getEnabledBlockedAppsCount(): Flow<Int> {
        return blockedAppDao.getEnabledBlockedAppsCount()
    }
    
    /**
     * Gets schedules for a specific app.
     */
    fun getSchedulesForApp(packageName: String): Flow<List<Schedule>> {
        return scheduleDao.getSchedulesForApp(packageName)
    }
    
    /**
     * Adds a blocked app with a schedule.
     */
    suspend fun addBlockedApp(
        packageName: String,
        appName: String,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int,
        daysOfWeek: Int,
        isAllDay: Boolean = false
    ) {
        // Insert or update the blocked app
        val blockedApp = BlockedApp(
            packageName = packageName,
            appName = appName,
            isEnabled = true
        )
        blockedAppDao.insert(blockedApp)
        
        // Delete existing schedules and add new one
        scheduleDao.deleteAllForApp(packageName)
        
        val schedule = Schedule(
            packageName = packageName,
            startHour = startHour,
            startMinute = startMinute,
            endHour = endHour,
            endMinute = endMinute,
            daysOfWeek = daysOfWeek,
            isAllDay = isAllDay,
            isEnabled = true
        )
        scheduleDao.insert(schedule)
    }
    
    /**
     * Updates a schedule.
     */
    suspend fun updateSchedule(schedule: Schedule) {
        scheduleDao.update(schedule)
    }
    
    /**
     * Removes a blocked app and all its schedules.
     */
    suspend fun removeBlockedApp(packageName: String) {
        blockedAppDao.deleteByPackageName(packageName)
        // Schedules are automatically deleted via foreign key CASCADE
    }
    
    /**
     * Toggles an app's enabled state.
     */
    suspend fun toggleAppEnabled(packageName: String, isEnabled: Boolean) {
        blockedAppDao.setEnabled(packageName, isEnabled)
    }
    
    /**
     * Checks if an app is already blocked.
     */
    suspend fun isAppBlocked(packageName: String): Boolean {
        return blockedAppDao.isAppBlocked(packageName)
    }
}
