package com.solvetoscroll.ui.screens

import android.app.AppOpsManager
import android.content.Context
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    
    companion object {
        val ACCESS_DURATION_KEY = intPreferencesKey("access_duration_minutes")
        const val DEFAULT_ACCESS_DURATION = 5 // minutes
        
        val DURATION_OPTIONS = listOf(1, 2, 3, 5, 10, 15, 30) // minutes
    }
    
    private val _hasUsageAccess = MutableStateFlow(false)
    val hasUsageAccess: StateFlow<Boolean> = _hasUsageAccess.asStateFlow()
    
    private val _hasOverlayPermission = MutableStateFlow(false)
    val hasOverlayPermission: StateFlow<Boolean> = _hasOverlayPermission.asStateFlow()
    
    private val _accessDurationMinutes = MutableStateFlow(DEFAULT_ACCESS_DURATION)
    val accessDurationMinutes: StateFlow<Int> = _accessDurationMinutes.asStateFlow()
    
    init {
        checkPermissions()
        loadAccessDuration()
        
        // Periodically check permissions
        viewModelScope.launch {
            while (true) {
                delay(1000)
                checkPermissions()
            }
        }
    }
    
    private fun loadAccessDuration() {
        viewModelScope.launch {
            val duration = dataStore.data.map { preferences ->
                preferences[ACCESS_DURATION_KEY] ?: DEFAULT_ACCESS_DURATION
            }.first()
            _accessDurationMinutes.value = duration
        }
    }
    
    fun setAccessDuration(minutes: Int) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[ACCESS_DURATION_KEY] = minutes
            }
            _accessDurationMinutes.value = minutes
        }
    }
    
    private fun checkPermissions() {
        _hasUsageAccess.value = checkUsageAccessPermission()
        _hasOverlayPermission.value = checkOverlayPermission()
    }
    
    private fun checkUsageAccessPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }
    
    private fun checkOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }
}
