package com.solvetoscroll.blocker

import javax.inject.Inject
import kotlin.time.Duration

/**
 * Manages temporary access grants after successfully completing challenges.
 */
class AccessGrantManager @Inject constructor() {
    
    // Map of package name to access expiration time (milliseconds)
    private val accessGrants = mutableMapOf<String, Long>()
    
    /**
     * Grants temporary access to an app for the specified duration.
     */
    fun grantAccess(packageName: String, duration: Duration) {
        val expirationTime = System.currentTimeMillis() + duration.inWholeMilliseconds
        accessGrants[packageName] = expirationTime
    }
    
    /**
     * Checks if an app currently has active access.
     */
    fun hasActiveAccess(packageName: String): Boolean {
        val expirationTime = accessGrants[packageName] ?: return false
        
        if (System.currentTimeMillis() >= expirationTime) {
            // Access has expired
            accessGrants.remove(packageName)
            return false
        }
        
        return true
    }
    
    /**
     * Gets the remaining access time in seconds for an app.
     * Returns 0 if no active access.
     */
    fun getRemainingAccessSeconds(packageName: String): Int {
        val expirationTime = accessGrants[packageName] ?: return 0
        val remaining = expirationTime - System.currentTimeMillis()
        
        if (remaining <= 0) {
            accessGrants.remove(packageName)
            return 0
        }
        
        return (remaining / 1000).toInt()
    }
    
    /**
     * Revokes access for an app.
     */
    fun revokeAccess(packageName: String) {
        accessGrants.remove(packageName)
    }
    
    /**
     * Clears all access grants.
     */
    fun clearAllAccess() {
        accessGrants.clear()
    }
    
    /**
     * Gets a list of all packages with active access.
     */
    fun getActiveAccessPackages(): List<String> {
        val now = System.currentTimeMillis()
        return accessGrants.filter { it.value > now }.keys.toList()
    }
}
