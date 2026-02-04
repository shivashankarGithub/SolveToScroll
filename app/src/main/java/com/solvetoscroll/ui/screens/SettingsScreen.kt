package com.solvetoscroll.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.solvetoscroll.BuildConfig
import com.solvetoscroll.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val hasUsageAccess by viewModel.hasUsageAccess.collectAsState()
    val hasOverlayPermission by viewModel.hasOverlayPermission.collectAsState()
    val accessDurationMinutes by viewModel.accessDurationMinutes.collectAsState()
    
    var showDurationDialog by remember { mutableStateOf(false) }
    
    if (showDurationDialog) {
        DurationPickerDialog(
            currentDuration = accessDurationMinutes,
            onDurationSelected = { minutes ->
                viewModel.setAccessDuration(minutes)
                showDurationDialog = false
            },
            onDismiss = { showDurationDialog = false }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
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
        ) {
            // Permissions Section
            Text(
                text = stringResource(R.string.settings_permissions),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            
            PermissionItem(
                title = stringResource(R.string.settings_usage_access),
                isGranted = hasUsageAccess,
                onClick = {
                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    context.startActivity(intent)
                }
            )
            
            PermissionItem(
                title = stringResource(R.string.settings_overlay),
                isGranted = hasOverlayPermission,
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:${context.packageName}")
                        )
                        context.startActivity(intent)
                    }
                }
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            
            // Access Duration Section
            Text(
                text = stringResource(R.string.settings_access_duration),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_unlock_duration)) },
                supportingContent = { 
                    Text(
                        if (accessDurationMinutes == 1) "1 minute" 
                        else "$accessDurationMinutes minutes"
                    ) 
                },
                modifier = Modifier.clickable { showDurationDialog = true }
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            
            // About Section
            Text(
                text = stringResource(R.string.settings_about),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_privacy_policy)) },
                modifier = Modifier.clickable {
                    // Open privacy policy URL
                }
            )
            
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_version)) },
                supportingContent = { Text(BuildConfig.VERSION_NAME) }
            )
        }
    }
}

@Composable
private fun PermissionItem(
    title: String,
    isGranted: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        trailingContent = {
            Icon(
                imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = if (isGranted) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun DurationPickerDialog(
    currentDuration: Int,
    onDurationSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_unlock_duration)) },
        text = {
            Column {
                SettingsViewModel.DURATION_OPTIONS.forEach { minutes ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDurationSelected(minutes) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = minutes == currentDuration,
                            onClick = { onDurationSelected(minutes) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (minutes == 1) "1 minute" else "$minutes minutes"
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
