package com.solvetoscroll.ui.challenge

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.solvetoscroll.R
import com.solvetoscroll.challenge.Challenge

@Composable
fun ReflectionChallengeUI(
    challenge: Challenge.Reflection,
    isError: Boolean,
    errorMessage: String?,
    onSubmit: (String) -> Unit
) {
    var response by remember { mutableStateOf("") }
    
    // Reset input when challenge changes
    LaunchedEffect(challenge) {
        response = ""
    }
    
    // Count words
    val wordCount = remember(response) {
        response.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Instruction
        Text(
            text = stringResource(R.string.reflection_instruction),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Prompt
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text = challenge.prompt,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(16.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Response input
        OutlinedTextField(
            value = response,
            onValueChange = { response = it },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            ),
            isError = isError,
            minLines = 4,
            maxLines = 6,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Word count
        Text(
            text = stringResource(R.string.reflection_word_count, wordCount, challenge.minimumWords),
            style = MaterialTheme.typography.labelSmall,
            color = if (wordCount >= challenge.minimumWords) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
        
        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Submit button
        Button(
            onClick = { onSubmit(response) },
            enabled = wordCount >= challenge.minimumWords,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text(stringResource(R.string.challenge_submit))
        }
    }
}
