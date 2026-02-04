package com.solvetoscroll.ui.challenge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solvetoscroll.R
import com.solvetoscroll.challenge.Challenge

@Composable
fun WordChallengeUI(
    challenge: Challenge.Word,
    onSubmit: (String) -> Unit,
    showError: Boolean = false
) {
    var userInput by remember { mutableStateOf("") }
    
    // Reset input when challenge changes or after error
    LaunchedEffect(challenge, showError) {
        if (showError) {
            // Clear input after a brief delay to show error
            kotlinx.coroutines.delay(500)
            userInput = ""
        }
    }
    
    // Reset when a new challenge is received
    LaunchedEffect(challenge) {
        userInput = ""
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Instructions
        Text(
            text = "Unscramble the word",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Rearrange the letters to form a word",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Scrambled letters display
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            challenge.scrambledWord.forEach { letter ->
                LetterTile(letter = letter)
                Spacer(modifier = Modifier.width(6.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Input field
        OutlinedTextField(
            value = userInput,
            onValueChange = { newValue ->
                // Only allow letters, limit to word length
                val filtered = newValue.filter { it.isLetter() }
                    .take(challenge.originalWord.length)
                    .uppercase()
                userInput = filtered
            },
            label = { Text("Your answer") },
            placeholder = { Text("Type the word...") },
            singleLine = true,
            isError = showError,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onSubmit(userInput) }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        // Character count
        Text(
            text = "${userInput.length} / ${challenge.originalWord.length} letters",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
        
        if (showError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Incorrect. Try again!",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Submit button
        Button(
            onClick = { onSubmit(userInput) },
            enabled = userInput.length == challenge.originalWord.length,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.challenge_submit))
        }
    }
}

@Composable
private fun LetterTile(letter: Char) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
