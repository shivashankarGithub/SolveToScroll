package com.solvetoscroll.ui.challenge

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.solvetoscroll.R
import com.solvetoscroll.challenge.Challenge

@Composable
fun TypingChallengeUI(
    challenge: Challenge.Typing,
    isError: Boolean,
    onSubmit: (String) -> Unit
) {
    var typedText by remember { mutableStateOf("") }
    
    // Reset input when challenge changes
    LaunchedEffect(challenge) {
        typedText = ""
    }
    
    val expectedText = challenge.textToType
    val isComplete = typedText == expectedText
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Instruction
        Text(
            text = stringResource(R.string.typing_instruction),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Text to type with character highlighting
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = buildHighlightedText(expectedText, typedText),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(16.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Typing input
        OutlinedTextField(
            value = typedText,
            onValueChange = { typedText = it },
            label = { Text(stringResource(R.string.typing_placeholder)) },
            keyboardOptions = KeyboardOptions(
                imeAction = if (isComplete) ImeAction.Done else ImeAction.Default
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (isComplete) {
                        onSubmit(typedText)
                    }
                }
            ),
            isError = isError,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Progress indicator
        Text(
            text = "${typedText.length} / ${expectedText.length} characters",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        if (isError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.challenge_incorrect),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Submit button
        Button(
            onClick = { onSubmit(typedText) },
            enabled = typedText.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text(stringResource(R.string.challenge_submit))
        }
    }
}

@Composable
private fun buildHighlightedText(expected: String, typed: String) = buildAnnotatedString {
    for (i in expected.indices) {
        val style = when {
            i >= typed.length -> SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)
            expected[i] == typed[i] -> SpanStyle(color = Color(0xFF4CAF50)) // Green for correct
            else -> SpanStyle(color = Color(0xFFF44336)) // Red for incorrect
        }
        withStyle(style) {
            append(expected[i])
        }
    }
}
