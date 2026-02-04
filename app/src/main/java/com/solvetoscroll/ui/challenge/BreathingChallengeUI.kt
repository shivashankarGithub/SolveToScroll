package com.solvetoscroll.ui.challenge

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.solvetoscroll.challenge.Challenge
import com.solvetoscroll.challenge.breathing.BreathingPhase
import kotlinx.coroutines.delay

@Composable
fun BreathingChallengeUI(
    challenge: Challenge.Breathing,
    onComplete: () -> Unit
) {
    var currentCycle by remember { mutableIntStateOf(1) }
    var currentPhase by remember { mutableStateOf(BreathingPhase.INHALE) }
    var phaseTimeRemaining by remember { mutableIntStateOf(challenge.inhaleSeconds) }
    var totalTimeRemaining by remember { mutableIntStateOf(challenge.totalDurationSeconds) }
    
    // Animation for the breathing circle
    val targetScale = when (currentPhase) {
        BreathingPhase.INHALE -> 1.5f
        BreathingPhase.HOLD -> 1.5f
        BreathingPhase.EXHALE -> 0.8f
        BreathingPhase.COMPLETE -> 1f
    }
    
    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(
            durationMillis = when (currentPhase) {
                BreathingPhase.INHALE -> challenge.inhaleSeconds * 1000
                BreathingPhase.EXHALE -> challenge.exhaleSeconds * 1000
                else -> 300
            },
            easing = if (currentPhase == BreathingPhase.INHALE || currentPhase == BreathingPhase.EXHALE) {
                LinearEasing
            } else {
                FastOutSlowInEasing
            }
        ),
        label = "breathing_scale"
    )
    
    // Timer logic
    LaunchedEffect(challenge) {
        currentCycle = 1
        currentPhase = BreathingPhase.INHALE
        phaseTimeRemaining = challenge.inhaleSeconds
        totalTimeRemaining = challenge.totalDurationSeconds
        
        while (currentCycle <= challenge.cycles && currentPhase != BreathingPhase.COMPLETE) {
            // Inhale phase
            currentPhase = BreathingPhase.INHALE
            for (i in challenge.inhaleSeconds downTo 1) {
                phaseTimeRemaining = i
                delay(1000)
                totalTimeRemaining--
            }
            
            // Hold phase
            currentPhase = BreathingPhase.HOLD
            for (i in challenge.holdSeconds downTo 1) {
                phaseTimeRemaining = i
                delay(1000)
                totalTimeRemaining--
            }
            
            // Exhale phase
            currentPhase = BreathingPhase.EXHALE
            for (i in challenge.exhaleSeconds downTo 1) {
                phaseTimeRemaining = i
                delay(1000)
                totalTimeRemaining--
            }
            
            currentCycle++
        }
        
        currentPhase = BreathingPhase.COMPLETE
        delay(500)
        onComplete()
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Breathing Exercise",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Follow the circle and breathe",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Cycle progress
        Text(
            text = "Cycle ${minOf(currentCycle, challenge.cycles)} of ${challenge.cycles}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Breathing circle
        Box(
            modifier = Modifier
                .size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer ring
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            )
            
            // Animated inner circle
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(animatedScale)
                    .clip(CircleShape)
                    .background(
                        when (currentPhase) {
                            BreathingPhase.INHALE -> MaterialTheme.colorScheme.primary
                            BreathingPhase.HOLD -> MaterialTheme.colorScheme.secondary
                            BreathingPhase.EXHALE -> MaterialTheme.colorScheme.tertiary
                            BreathingPhase.COMPLETE -> MaterialTheme.colorScheme.primary
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = phaseTimeRemaining.toString(),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Phase instruction
        Text(
            text = when (currentPhase) {
                BreathingPhase.INHALE -> "Breathe In..."
                BreathingPhase.HOLD -> "Hold..."
                BreathingPhase.EXHALE -> "Breathe Out..."
                BreathingPhase.COMPLETE -> "Complete!"
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            color = when (currentPhase) {
                BreathingPhase.INHALE -> MaterialTheme.colorScheme.primary
                BreathingPhase.HOLD -> MaterialTheme.colorScheme.secondary
                BreathingPhase.EXHALE -> MaterialTheme.colorScheme.tertiary
                BreathingPhase.COMPLETE -> MaterialTheme.colorScheme.primary
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Total time remaining
        Text(
            text = "${totalTimeRemaining}s remaining",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Note about not being able to skip
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = "Take this moment to pause and breathe. This exercise cannot be skipped.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
