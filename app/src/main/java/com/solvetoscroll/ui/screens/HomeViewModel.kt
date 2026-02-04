package com.solvetoscroll.ui.screens

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solvetoscroll.blocker.BlockerService
import com.solvetoscroll.data.entities.BlockedApp
import com.solvetoscroll.scheduler.ScheduleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scheduleManager: ScheduleManager,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    
    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }
    
    val blockedApps: StateFlow<List<BlockedApp>> = scheduleManager.getAllBlockedApps()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    private val _hasCompletedOnboarding = MutableStateFlow(true) // Default to true, update from datastore
    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding
    
    init {
        viewModelScope.launch {
            _hasCompletedOnboarding.value = dataStore.data.map { preferences ->
                preferences[ONBOARDING_COMPLETED] ?: false
            }.first()
        }
    }
    
    fun completeOnboarding() {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[ONBOARDING_COMPLETED] = true
            }
            _hasCompletedOnboarding.value = true
            
            // Start the blocker service
            startBlockerService()
        }
    }
    
    fun removeBlockedApp(packageName: String) {
        viewModelScope.launch {
            scheduleManager.removeBlockedApp(packageName)
        }
    }
    
    fun toggleAppEnabled(packageName: String, isEnabled: Boolean) {
        viewModelScope.launch {
            scheduleManager.toggleAppEnabled(packageName, isEnabled)
        }
    }
    
    private fun startBlockerService() {
        val serviceIntent = Intent(context, BlockerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}
