package com.solvetoscroll.scheduler

import com.solvetoscroll.data.dao.BlockedAppDao
import com.solvetoscroll.data.dao.ScheduleDao
import com.solvetoscroll.data.entities.Schedule
import java.util.Calendar
import javax.inject.Inject

/**
 * Checks if an app is currently blocked based on schedules.
 */
class ScheduleChecker @Inject constructor(
    private val blockedAppDao: BlockedAppDao,
    private val scheduleDao: ScheduleDao
) {
    
    /**
     * Checks if an app should be blocked right now.
     */
    suspend fun isBlockedNow(packageName: String): Boolean {
        // First check if the app is in the blocked list and enabled
        if (!blockedAppDao.isAppBlocked(packageName)) {
            return false
        }
        
        // Get all enabled schedules for this app
        val schedules = scheduleDao.getSchedulesForAppSync(packageName)
        
        if (schedules.isEmpty()) {
            return false
        }
        
        // Check if any schedule is active right now
        return schedules.any { isScheduleActive(it) }
    }
    
    /**
     * Checks if a schedule is currently active.
     */
    private fun isScheduleActive(schedule: Schedule): Boolean {
        if (!schedule.isEnabled) return false
        
        val now = Calendar.getInstance()
        val currentDayOfWeek = getDayBitmask(now.get(Calendar.DAY_OF_WEEK))
        
        // Check if today is included in the schedule
        if (!schedule.isDayEnabled(currentDayOfWeek)) {
            return false
        }
        
        // If all day, it's active
        if (schedule.isAllDay) {
            return true
        }
        
        // Check if current time is within the schedule
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMinute = now.get(Calendar.MINUTE)
        val currentTimeMinutes = currentHour * 60 + currentMinute
        
        val startTimeMinutes = schedule.startHour * 60 + schedule.startMinute
        val endTimeMinutes = schedule.endHour * 60 + schedule.endMinute
        
        return if (startTimeMinutes <= endTimeMinutes) {
            // Normal case: start and end on same day
            currentTimeMinutes in startTimeMinutes until endTimeMinutes
        } else {
            // Overnight case: e.g., 22:00 to 06:00
            currentTimeMinutes >= startTimeMinutes || currentTimeMinutes < endTimeMinutes
        }
    }
    
    /**
     * Converts Calendar.DAY_OF_WEEK to our bitmask format.
     */
    private fun getDayBitmask(calendarDay: Int): Int {
        return when (calendarDay) {
            Calendar.MONDAY -> Schedule.MONDAY
            Calendar.TUESDAY -> Schedule.TUESDAY
            Calendar.WEDNESDAY -> Schedule.WEDNESDAY
            Calendar.THURSDAY -> Schedule.THURSDAY
            Calendar.FRIDAY -> Schedule.FRIDAY
            Calendar.SATURDAY -> Schedule.SATURDAY
            Calendar.SUNDAY -> Schedule.SUNDAY
            else -> 0
        }
    }
}
