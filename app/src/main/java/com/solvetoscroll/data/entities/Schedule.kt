package com.solvetoscroll.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "schedules",
    foreignKeys = [
        ForeignKey(
            entity = BlockedApp::class,
            parentColumns = ["packageName"],
            childColumns = ["packageName"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("packageName")]
)
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val startHour: Int,          // 0-23
    val startMinute: Int,        // 0-59
    val endHour: Int,            // 0-23
    val endMinute: Int,          // 0-59
    val daysOfWeek: Int,         // Bitmask: Mon=1, Tue=2, Wed=4, Thu=8, Fri=16, Sat=32, Sun=64
    val isAllDay: Boolean = false,
    val isEnabled: Boolean = true
) {
    companion object {
        const val MONDAY = 1
        const val TUESDAY = 2
        const val WEDNESDAY = 4
        const val THURSDAY = 8
        const val FRIDAY = 16
        const val SATURDAY = 32
        const val SUNDAY = 64
        const val ALL_DAYS = 127 // All days selected
        const val WEEKDAYS = 31  // Mon-Fri
        const val WEEKENDS = 96  // Sat-Sun
    }
    
    fun isDayEnabled(day: Int): Boolean = (daysOfWeek and day) != 0
    
    fun getEnabledDays(): List<Int> {
        return listOf(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY)
            .filter { isDayEnabled(it) }
    }
}
