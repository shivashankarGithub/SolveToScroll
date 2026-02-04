package com.solvetoscroll.blocker

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import javax.inject.Inject

/**
 * Monitors app usage to detect when blocked apps are opened.
 */
class UsageMonitor @Inject constructor(
    private val context: Context
) {
    
    private val usageStatsManager: UsageStatsManager by lazy {
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    }
    
    private val packageManager: PackageManager by lazy {
        context.packageManager
    }
    
    /**
     * Gets the currently foreground app's package name.
     * Returns null if unable to determine.
     */
    fun getForegroundPackage(): String? {
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 1000 // Last 1 second
        
        val usageEvents = usageStatsManager.queryEvents(beginTime, endTime)
        val event = UsageEvents.Event()
        
        var foregroundPackage: String? = null
        
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED ||
                event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                foregroundPackage = event.packageName
            }
        }
        
        // If no recent event, try getting from usage stats
        if (foregroundPackage == null) {
            foregroundPackage = getForegroundPackageFromStats()
        }
        
        return foregroundPackage
    }
    
    private fun getForegroundPackageFromStats(): String? {
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 60000 // Last minute
        
        val usageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            beginTime,
            endTime
        )
        
        if (usageStats.isNullOrEmpty()) return null
        
        // Find the most recently used app
        return usageStats
            .filter { it.lastTimeUsed > beginTime }
            .maxByOrNull { it.lastTimeUsed }
            ?.packageName
    }
    
    /**
     * Checks if the app has usage access permission.
     */
    fun hasUsageAccessPermission(): Boolean {
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 60000
        
        val usageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            beginTime,
            endTime
        )
        
        return usageStats.isNotEmpty()
    }
    
    /**
     * Gets the app name for a package.
     */
    fun getAppName(packageName: String): String {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName
        }
    }
    
    /**
     * Gets the list of all launchable apps on the device.
     */
    fun getLaunchableApps(): List<AppInfo> {
        val mainIntent = android.content.Intent(android.content.Intent.ACTION_MAIN, null)
        mainIntent.addCategory(android.content.Intent.CATEGORY_LAUNCHER)
        
        val resolveInfoList = packageManager.queryIntentActivities(mainIntent, 0)
        
        return resolveInfoList
            .map { resolveInfo ->
                val packageName = resolveInfo.activityInfo.packageName
                AppInfo(
                    packageName = packageName,
                    appName = resolveInfo.loadLabel(packageManager).toString(),
                    icon = resolveInfo.loadIcon(packageManager)
                )
            }
            .filter { it.packageName != context.packageName } // Exclude our own app
            .sortedBy { it.appName.lowercase() }
    }
}

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: android.graphics.drawable.Drawable
)
