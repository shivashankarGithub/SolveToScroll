package com.solvetoscroll.ui.screens

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solvetoscroll.blocker.UsageMonitor
import com.solvetoscroll.data.entities.Schedule
import com.solvetoscroll.scheduler.ScheduleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScheduleUiState(
    val packageName: String = "",
    val appName: String = "",
    val startHour: Int = 9,
    val startMinute: Int = 0,
    val endHour: Int = 17,
    val endMinute: Int = 0,
    val selectedDays: Int = Schedule.ALL_DAYS,
    val isAllDay: Boolean = false,
    val isSaved: Boolean = false
)

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scheduleManager: ScheduleManager,
    private val usageMonitor: UsageMonitor
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()
    
    fun initialize(packageName: String) {
        val appName = usageMonitor.getAppName(packageName)
        
        _uiState.update {
            it.copy(
                packageName = packageName,
                appName = appName
            )
        }
    }
    
    fun setStartTime(hour: Int, minute: Int) {
        _uiState.update { it.copy(startHour = hour, startMinute = minute) }
    }
    
    fun setEndTime(hour: Int, minute: Int) {
        _uiState.update { it.copy(endHour = hour, endMinute = minute) }
    }
    
    fun toggleDay(day: Int) {
        _uiState.update {
            val newDays = it.selectedDays xor day
            it.copy(selectedDays = newDays)
        }
    }
    
    fun setAllDay(isAllDay: Boolean) {
        _uiState.update { it.copy(isAllDay = isAllDay) }
    }
    
    fun save() {
        viewModelScope.launch {
            val state = _uiState.value
            
            scheduleManager.addBlockedApp(
                packageName = state.packageName,
                appName = state.appName,
                startHour = if (state.isAllDay) 0 else state.startHour,
                startMinute = if (state.isAllDay) 0 else state.startMinute,
                endHour = if (state.isAllDay) 23 else state.endHour,
                endMinute = if (state.isAllDay) 59 else state.endMinute,
                daysOfWeek = state.selectedDays,
                isAllDay = state.isAllDay
            )
            
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}
