package com.solvetoscroll.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attempt_records",
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
data class AttemptRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val attemptCount: Int = 0,
    val lastAttemptTime: Long = System.currentTimeMillis(),
    val lastSuccessTime: Long? = null
) {
    companion object {
        // Reset attempt count after 24 hours of no attempts
        const val RESET_THRESHOLD_MS = 24 * 60 * 60 * 1000L
    }
    
    fun shouldResetAttempts(): Boolean {
        return System.currentTimeMillis() - lastAttemptTime > RESET_THRESHOLD_MS
    }
}
