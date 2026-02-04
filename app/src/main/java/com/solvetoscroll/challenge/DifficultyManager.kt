package com.solvetoscroll.challenge

import com.solvetoscroll.data.dao.AttemptDao
import com.solvetoscroll.data.entities.AttemptRecord
import javax.inject.Inject

/**
 * Manages difficulty escalation based on bypass attempt count.
 */
class DifficultyManager @Inject constructor(
    private val attemptDao: AttemptDao
) {
    
    /**
     * Gets the current difficulty level for an app based on attempt count.
     * 
     * Difficulty progression (starts at level 2 - no easy mode):
     * - Attempts 0: Level 2 (Linear equations)
     * - Attempts 1-2: Level 3 (Multi-step equations)
     * - Attempts 3+: Level 4 (Quadratic equations)
     */
    fun getDifficulty(attemptCount: Int): Int {
        return when {
            attemptCount == 0 -> 2
            attemptCount <= 2 -> 3
            else -> 4
        }
    }
    
    /**
     * Gets the current attempt count for a package, handling resets.
     */
    suspend fun getAttemptCount(packageName: String): Int {
        val record = attemptDao.getAttemptRecord(packageName)
        
        if (record == null) {
            // Create a new record
            attemptDao.insert(AttemptRecord(packageName = packageName, attemptCount = 0))
            return 0
        }
        
        // Check if we should reset due to 24 hour threshold
        if (record.shouldResetAttempts()) {
            attemptDao.resetAttempts(packageName)
            return 0
        }
        
        return record.attemptCount
    }
    
    /**
     * Increments the attempt count for a package.
     */
    suspend fun incrementAttempt(packageName: String) {
        val record = attemptDao.getAttemptRecord(packageName)
        
        if (record == null) {
            attemptDao.insert(
                AttemptRecord(
                    packageName = packageName,
                    attemptCount = 1,
                    lastAttemptTime = System.currentTimeMillis()
                )
            )
        } else {
            attemptDao.incrementAttempt(packageName)
        }
    }
    
    /**
     * Resets the attempt count after a successful challenge completion.
     */
    suspend fun resetAttempts(packageName: String) {
        attemptDao.resetAttempts(packageName, System.currentTimeMillis())
    }
    
    /**
     * Gets the current difficulty level for a package.
     */
    suspend fun getDifficultyForPackage(packageName: String): Int {
        val attemptCount = getAttemptCount(packageName)
        return getDifficulty(attemptCount)
    }
}
