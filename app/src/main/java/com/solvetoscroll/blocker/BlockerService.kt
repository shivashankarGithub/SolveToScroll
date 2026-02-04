package com.solvetoscroll.blocker

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.solvetoscroll.MainActivity
import com.solvetoscroll.R
import com.solvetoscroll.SolveToScrollApplication
import com.solvetoscroll.data.dao.BlockedAppDao
import com.solvetoscroll.scheduler.ScheduleChecker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BlockerService : Service() {
    
    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val POLLING_INTERVAL_MS = 500L
        
        // Packages that should never be blocked
        private val SYSTEM_PACKAGES = setOf(
            "com.android.systemui",
            "com.android.launcher",
            "com.android.launcher3",
            "com.google.android.apps.nexuslauncher",
            "com.android.settings",
            "com.android.vending", // Play Store
            "com.android.phone",
            "com.android.dialer"
        )
    }
    
    @Inject lateinit var usageMonitor: UsageMonitor
    @Inject lateinit var scheduleChecker: ScheduleChecker
    @Inject lateinit var accessGrantManager: AccessGrantManager
    @Inject lateinit var blockedAppDao: BlockedAppDao
    
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    private var lastBlockedPackage: String? = null
    private var lastBlockTime: Long = 0
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onCreate() {
        super.onCreate()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification())
        startMonitoring()
        return START_STICKY
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        
        // Schedule service restart
        scheduleServiceRestart()
    }
    
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        
        // When app is swiped away from recents, restart the service
        scheduleServiceRestart()
    }
    
    private fun scheduleServiceRestart() {
        val restartIntent = Intent(this, BlockerService::class.java).apply {
            setPackage(packageName)
        }
        
        val pendingIntent = PendingIntent.getService(
            this,
            1,
            restartIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 1000, // Restart after 1 second
            pendingIntent
        )
    }
    
    private fun startMonitoring() {
        serviceScope.launch {
            while (isActive) {
                try {
                    val foregroundPackage = usageMonitor.getForegroundPackage()
                    
                    if (foregroundPackage != null && shouldBlock(foregroundPackage)) {
                        // Debounce: Don't show overlay too frequently for the same app
                        val now = System.currentTimeMillis()
                        if (foregroundPackage != lastBlockedPackage || 
                            now - lastBlockTime > 2000) {
                            
                            lastBlockedPackage = foregroundPackage
                            lastBlockTime = now
                            showBlockOverlay(foregroundPackage)
                        }
                    }
                } catch (e: Exception) {
                    // Log error but continue monitoring
                    e.printStackTrace()
                }
                
                delay(POLLING_INTERVAL_MS)
            }
        }
    }
    
    private suspend fun shouldBlock(packageName: String): Boolean {
        // Don't block ourselves
        if (packageName == this.packageName) return false
        
        // Don't block system packages
        if (packageName in SYSTEM_PACKAGES) return false
        
        // Check if user has temporary access
        if (accessGrantManager.hasActiveAccess(packageName)) return false
        
        // Check if app is blocked and within schedule
        return scheduleChecker.isBlockedNow(packageName)
    }
    
    private fun showBlockOverlay(packageName: String) {
        val intent = Intent(this, BlockOverlayActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(BlockOverlayActivity.EXTRA_BLOCKED_PACKAGE, packageName)
        }
        startActivity(intent)
    }
    
    private fun buildNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        
        // Get blocked app count
        var blockedCount = 0
        serviceScope.launch {
            blockedCount = blockedAppDao.getEnabledBlockedAppsCount().first()
            updateNotification(blockedCount)
        }
        
        return NotificationCompat.Builder(this, SolveToScrollApplication.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text, blockedCount))
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    private fun updateNotification(blockedCount: Int) {
        val notification = NotificationCompat.Builder(this, SolveToScrollApplication.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text, blockedCount))
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        
        val notificationManager = getSystemService(android.app.NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
