package com.solvetoscroll.ui.challenge

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.solvetoscroll.R
import com.solvetoscroll.challenge.Challenge
import com.solvetoscroll.ui.components.AppInfoHeader

@Composable
fun ChallengeScreen(
    blockedPackage: String,
    onAccessGranted: () -> Unit,
    onDismiss: () -> Unit,
    viewModel: ChallengeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(blockedPackage) {
        viewModel.initialize(blockedPackage)
    }
    
    // Handle success
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onAccessGranted()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        // App info header
        AppInfoHeader(packageName = blockedPackage)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Blocked message
        Text(
            text = stringResource(R.string.challenge_blocked_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = stringResource(R.string.challenge_blocked_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Difficulty indicator
        DifficultyIndicator(difficulty = uiState.difficulty)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Main challenge content
        if (uiState.waitTimeRemaining > 0) {
            WaitTimerUI(secondsRemaining = uiState.waitTimeRemaining)
        } else {
            when (val challenge = uiState.currentChallenge) {
                is Challenge.Math -> MathChallengeUI(
                    challenge = challenge,
                    isError = uiState.showError,
                    onSubmit = { answer -> viewModel.submitMathAnswer(answer) }
                )
                is Challenge.Typing -> TypingChallengeUI(
                    challenge = challenge,
                    isError = uiState.showError,
                    onSubmit = { typed -> viewModel.submitTyping(typed) }
                )
                is Challenge.Reflection -> ReflectionChallengeUI(
                    challenge = challenge,
                    isError = uiState.showError,
                    errorMessage = uiState.errorMessage,
                    onSubmit = { response -> viewModel.submitReflection(response) }
                )
                is Challenge.Memory -> MemoryChallengeUI(
                    challenge = challenge,
                    onSequenceComplete = { sequence -> viewModel.submitMemorySequence(sequence) },
                    showError = uiState.showError
                )
                is Challenge.Word -> WordChallengeUI(
                    challenge = challenge,
                    onSubmit = { answer -> viewModel.submitWord(answer) },
                    showError = uiState.showError
                )
                is Challenge.Breathing -> BreathingChallengeUI(
                    challenge = challenge,
                    onComplete = { viewModel.onBreathingComplete() }
                )
                null -> {
                    CircularProgressIndicator()
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Attempt counter
        if (uiState.attemptCount > 0) {
            Text(
                text = stringResource(R.string.challenge_attempts, uiState.attemptCount),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Go back button
        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.challenge_go_back))
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun DifficultyIndicator(difficulty: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${stringResource(R.string.challenge_difficulty)}: ",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        repeat(4) { index ->
            Text(
                text = if (index < difficulty) "★" else "☆",
                style = MaterialTheme.typography.labelLarge,
                color = if (index < difficulty) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                }
            )
        }
    }
}

@Composable
fun WaitTimerUI(secondsRemaining: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = stringResource(R.string.wait_timer_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(R.string.wait_timer_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = stringResource(R.string.wait_timer_seconds, secondsRemaining),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }
}
