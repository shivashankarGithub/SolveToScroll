package com.solvetoscroll.ui.challenge

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.solvetoscroll.R
import com.solvetoscroll.challenge.Challenge

@Composable
fun MathChallengeUI(
    challenge: Challenge.Math,
    isError: Boolean,
    onSubmit: (Int?) -> Unit
) {
    var userAnswer by remember { mutableStateOf("") }
    
    // Reset input when challenge changes
    LaunchedEffect(challenge) {
        userAnswer = ""
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Equation display
        Text(
            text = challenge.displayText,
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Question prompt
        Text(
            text = challenge.questionPrompt,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Answer input
        OutlinedTextField(
            value = userAnswer,
            onValueChange = { input ->
                // Only allow digits and minus sign
                userAnswer = input.filter { it.isDigit() || (it == '-' && input.indexOf('-') == 0) }
            },
            label = { Text(stringResource(R.string.math_your_answer)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onSubmit(userAnswer.toIntOrNull())
                }
            ),
            isError = isError,
            singleLine = true,
            modifier = Modifier.width(200.dp)
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
            onClick = { onSubmit(userAnswer.toIntOrNull()) },
            enabled = userAnswer.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text(stringResource(R.string.challenge_submit))
        }
    }
}
