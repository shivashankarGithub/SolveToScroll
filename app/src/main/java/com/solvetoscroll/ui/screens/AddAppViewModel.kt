package com.solvetoscroll.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solvetoscroll.blocker.AppInfo
import com.solvetoscroll.blocker.UsageMonitor
import com.solvetoscroll.scheduler.ScheduleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAppViewModel @Inject constructor(
    private val usageMonitor: UsageMonitor,
    private val scheduleManager: ScheduleManager
) : ViewModel() {
    
    private val _allApps = MutableStateFlow<List<AppInfo>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _filteredApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val filteredApps: StateFlow<List<AppInfo>> = _filteredApps.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadApps()
        
        viewModelScope.launch {
            combine(_allApps, _searchQuery) { apps, query ->
                if (query.isBlank()) {
                    apps
                } else {
                    apps.filter { app ->
                        app.appName.contains(query, ignoreCase = true) ||
                        app.packageName.contains(query, ignoreCase = true)
                    }
                }
            }.collect { filtered ->
                _filteredApps.value = filtered
            }
        }
    }
    
    private fun loadApps() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            
            val launchableApps = usageMonitor.getLaunchableApps()
            
            // Filter out already blocked apps
            val blockedPackages = mutableSetOf<String>()
            scheduleManager.getAllBlockedApps().collect { blockedApps ->
                blockedPackages.clear()
                blockedPackages.addAll(blockedApps.map { it.packageName })
                
                _allApps.value = launchableApps.filter { it.packageName !in blockedPackages }
                _isLoading.value = false
            }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
