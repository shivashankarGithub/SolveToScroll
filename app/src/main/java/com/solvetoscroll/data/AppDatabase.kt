package com.solvetoscroll.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.solvetoscroll.data.dao.AttemptDao
import com.solvetoscroll.data.dao.BlockedAppDao
import com.solvetoscroll.data.dao.ScheduleDao
import com.solvetoscroll.data.entities.AttemptRecord
import com.solvetoscroll.data.entities.BlockedApp
import com.solvetoscroll.data.entities.Schedule

@Database(
    entities = [
        BlockedApp::class,
        Schedule::class,
        AttemptRecord::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun blockedAppDao(): BlockedAppDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun attemptDao(): AttemptDao
}
