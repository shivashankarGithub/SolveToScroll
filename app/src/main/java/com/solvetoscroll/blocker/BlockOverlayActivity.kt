package com.solvetoscroll.blocker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.solvetoscroll.ui.challenge.ChallengeScreen
import com.solvetoscroll.ui.screens.SettingsViewModel
import com.solvetoscroll.ui.theme.SolveToScrollTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@AndroidEntryPoint
class BlockOverlayActivity : ComponentActivity() {
    
    companion object {
        const val EXTRA_BLOCKED_PACKAGE = "blocked_package"
    }
    
    @Inject lateinit var accessGrantManager: AccessGrantManager
    @Inject lateinit var dataStore: DataStore<Preferences>
    
    private var blockedPackage: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        blockedPackage = intent.getStringExtra(EXTRA_BLOCKED_PACKAGE) ?: run {
            finish()
            return
        }
        
        setContent {
            SolveToScrollTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var accessDurationMinutes by remember { 
                        mutableIntStateOf(SettingsViewModel.DEFAULT_ACCESS_DURATION) 
                    }
                    
                    LaunchedEffect(Unit) {
                        accessDurationMinutes = dataStore.data.map { preferences ->
                            preferences[SettingsViewModel.ACCESS_DURATION_KEY] 
                                ?: SettingsViewModel.DEFAULT_ACCESS_DURATION
                        }.first()
                    }
                    
                    ChallengeScreen(
                        blockedPackage = blockedPackage,
                        onAccessGranted = {
                            // Grant access based on user setting
                            accessGrantManager.grantAccess(blockedPackage, accessDurationMinutes.minutes)
                            
                            // Return to the blocked app
                            val launchIntent = packageManager.getLaunchIntentForPackage(blockedPackage)
                            launchIntent?.let {
                                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(it)
                            }
                            finish()
                        },
                        onDismiss = {
                            // Go to home screen
                            goHome()
                        }
                    )
                }
            }
        }
    }
    
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Don't allow back button to bypass - go home instead
        goHome()
    }
    
    private fun goHome() {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeIntent)
        finish()
    }
}
