package com.solvetoscroll.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.solvetoscroll.R
import com.solvetoscroll.data.entities.Schedule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    packageName: String,
    onSave: () -> Unit,
    onBack: () -> Unit,
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(packageName) {
        viewModel.initialize(packageName)
    }
    
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onSave()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.schedule_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // App name
            Text(
                text = "Blocking: ${uiState.appName}",
                style = MaterialTheme.typography.titleMedium
            )
            
            // All day toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.schedule_all_day))
                Switch(
                    checked = uiState.isAllDay,
                    onCheckedChange = { viewModel.setAllDay(it) }
                )
            }
            
            // Time pickers (only if not all day)
            if (!uiState.isAllDay) {
                TimePickerSection(
                    label = stringResource(R.string.schedule_start_time),
                    hour = uiState.startHour,
                    minute = uiState.startMinute,
                    onTimeSelected = { hour, minute ->
                        viewModel.setStartTime(hour, minute)
                    }
                )
                
                TimePickerSection(
                    label = stringResource(R.string.schedule_end_time),
                    hour = uiState.endHour,
                    minute = uiState.endMinute,
                    onTimeSelected = { hour, minute ->
                        viewModel.setEndTime(hour, minute)
                    }
                )
            }
            
            // Day selector
            Text(
                text = stringResource(R.string.schedule_days),
                style = MaterialTheme.typography.labelLarge
            )
            
            DaySelector(
                selectedDays = uiState.selectedDays,
                onDayToggled = { day -> viewModel.toggleDay(day) }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Save button
            Button(
                onClick = { viewModel.save() },
                enabled = uiState.selectedDays != 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.schedule_save))
            }
        }
    }
}

@Composable
private fun TimePickerSection(
    label: String,
    hour: Int,
    minute: Int,
    onTimeSelected: (Int, Int) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }
    
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedButton(
            onClick = { showPicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = String.format("%02d:%02d", hour, minute),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
    
    if (showPicker) {
        TimePickerDialog(
            initialHour = hour,
            initialMinute = minute,
            onDismiss = { showPicker = false },
            onConfirm = { h, m ->
                onTimeSelected(h, m)
                showPicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(timePickerState.hour, timePickerState.minute)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}

@Composable
private fun DaySelector(
    selectedDays: Int,
    onDayToggled: (Int) -> Unit
) {
    val days = listOf(
        Schedule.MONDAY to stringResource(R.string.day_monday),
        Schedule.TUESDAY to stringResource(R.string.day_tuesday),
        Schedule.WEDNESDAY to stringResource(R.string.day_wednesday),
        Schedule.THURSDAY to stringResource(R.string.day_thursday),
        Schedule.FRIDAY to stringResource(R.string.day_friday),
        Schedule.SATURDAY to stringResource(R.string.day_saturday),
        Schedule.SUNDAY to stringResource(R.string.day_sunday)
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        days.forEach { (dayBit, dayName) ->
            val isSelected = (selectedDays and dayBit) != 0
            
            FilterChip(
                selected = isSelected,
                onClick = { onDayToggled(dayBit) },
                label = { Text(dayName) }
            )
        }
    }
}
