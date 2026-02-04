package com.solvetoscroll.challenge

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Manages wait timers that are enforced after multiple failed attempts.
 */
class WaitTimerManager {
    
    // Track when waits started for each package
    private val waitStartTimes = mutableMapOf<String, Long>()
    private val waitDurations = mutableMapOf<String, Long>()
    
    /**
     * Gets the required wait time based on attempt count.
     * 
     * Wait time progression:
     * - Attempts 0-4: No wait
     * - Attempts 5-7: 30 seconds
     * - Attempts 8-9: 60 seconds
     * - Attempts 10+: 120 seconds
     */
    fun getRequiredWaitTime(attemptCount: Int): Duration {
        return when {
            attemptCount < 5 -> Duration.ZERO
            attemptCount < 8 -> 30.seconds
            attemptCount < 10 -> 60.seconds
            else -> 120.seconds
        }
    }
    
    /**
     * Starts the wait timer for a package.
     */
    fun startWait(packageName: String, attemptCount: Int) {
        val waitDuration = getRequiredWaitTime(attemptCount)
        if (waitDuration > Duration.ZERO) {
            waitStartTimes[packageName] = System.currentTimeMillis()
            waitDurations[packageName] = waitDuration.inWholeMilliseconds
        }
    }
    
    /**
     * Gets the remaining wait time in seconds for a package.
     * Returns 0 if no wait is required.
     */
    fun getRemainingWaitSeconds(packageName: String): Int {
        val startTime = waitStartTimes[packageName] ?: return 0
        val duration = waitDurations[packageName] ?: return 0
        
        val elapsed = System.currentTimeMillis() - startTime
        val remaining = duration - elapsed
        
        if (remaining <= 0) {
            clearWait(packageName)
            return 0
        }
        
        return (remaining / 1000).toInt()
    }
    
    /**
     * Checks if a wait is currently active for a package.
     */
    fun isWaitActive(packageName: String): Boolean {
        return getRemainingWaitSeconds(packageName) > 0
    }
    
    /**
     * Clears the wait for a package.
     */
    fun clearWait(packageName: String) {
        waitStartTimes.remove(packageName)
        waitDurations.remove(packageName)
    }
    
    /**
     * Clears all waits.
     */
    fun clearAllWaits() {
        waitStartTimes.clear()
        waitDurations.clear()
    }
}
