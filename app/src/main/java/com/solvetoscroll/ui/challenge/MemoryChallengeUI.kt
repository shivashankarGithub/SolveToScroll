package com.solvetoscroll.ui.challenge

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.solvetoscroll.challenge.Challenge
import kotlinx.coroutines.delay

// The 6 colors for memory tiles
private val TILE_COLORS = listOf(
    Color(0xFFE57373), // Red
    Color(0xFF81C784), // Green
    Color(0xFF64B5F6), // Blue
    Color(0xFFFFD54F), // Yellow
    Color(0xFFBA68C8), // Purple
    Color(0xFFFF8A65)  // Orange
)

private val TILE_INACTIVE = Color(0xFF424242)
private val TILE_FLASH = Color.White

@Composable
fun MemoryChallengeUI(
    challenge: Challenge.Memory,
    onSequenceComplete: (List<Int>) -> Unit,
    showError: Boolean = false
) {
    var phase by remember { mutableStateOf(MemoryPhase.WATCHING) }
    var currentFlashIndex by remember { mutableIntStateOf(-1) }
    var userSequence by remember { mutableStateOf(listOf<Int>()) }
    var replayKey by remember { mutableIntStateOf(0) }
    
    // Reset when error is shown (wrong answer submitted)
    LaunchedEffect(showError, replayKey) {
        if (showError) {
            // Reset for a new attempt
            delay(1000)
            userSequence = emptyList()
            phase = MemoryPhase.WATCHING
            replayKey++
        }
    }
    
    // Flash sequence animation
    LaunchedEffect(challenge, replayKey) {
        phase = MemoryPhase.WATCHING
        userSequence = emptyList()
        currentFlashIndex = -1
        
        // Brief pause before starting
        delay(500)
        
        // Flash each item in sequence
        for ((index, _) in challenge.sequence.withIndex()) {
            currentFlashIndex = index
            delay(600) // Show the item
            currentFlashIndex = -1
            delay(200) // Gap between flashes
        }
        
        // Switch to input phase
        delay(300)
        phase = MemoryPhase.RECALLING
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Instructions
        Text(
            text = when (phase) {
                MemoryPhase.WATCHING -> "Watch the sequence..."
                MemoryPhase.RECALLING -> "Tap the colors in order"
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Progress indicator
        if (phase == MemoryPhase.RECALLING) {
            Text(
                text = "${userSequence.size} / ${challenge.sequence.size}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            // Show which item is being displayed
            val displayIndex = if (currentFlashIndex >= 0) currentFlashIndex + 1 else 0
            Text(
                text = "$displayIndex / ${challenge.sequence.size}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 3x2 grid of color tiles
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            for (row in 0 until 2) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (col in 0 until 3) {
                        val index = row * 3 + col
                        MemoryTile(
                            color = TILE_COLORS[index],
                            isFlashing = phase == MemoryPhase.WATCHING && 
                                currentFlashIndex >= 0 && 
                                challenge.sequence[currentFlashIndex] == index,
                            isEnabled = phase == MemoryPhase.RECALLING,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                if (phase == MemoryPhase.RECALLING) {
                                    val newSequence = userSequence + index
                                    userSequence = newSequence
                                    
                                    // Check if sequence is complete
                                    if (newSequence.size == challenge.sequence.size) {
                                        onSequenceComplete(newSequence)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
        
        if (showError) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Incorrect sequence. Try again!",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun MemoryTile(
    color: Color,
    isFlashing: Boolean,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = when {
            isFlashing -> TILE_FLASH
            isEnabled -> color
            else -> color.copy(alpha = 0.4f)
        },
        animationSpec = tween(150),
        label = "tile_color"
    )
    
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(animatedColor)
            .then(
                if (isEnabled) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isFlashing) {
            // Show a pulse effect when flashing
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color)
            )
        }
    }
}

private enum class MemoryPhase {
    WATCHING,
    RECALLING
}
