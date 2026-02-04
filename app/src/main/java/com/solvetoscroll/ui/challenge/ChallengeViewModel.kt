package com.solvetoscroll.ui.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solvetoscroll.challenge.Challenge
import com.solvetoscroll.challenge.ChallengeSelector
import com.solvetoscroll.challenge.DifficultyManager
import com.solvetoscroll.challenge.WaitTimerManager
import com.solvetoscroll.challenge.math.MathChallengeGenerator
import com.solvetoscroll.challenge.memory.MemoryChallengeGenerator
import com.solvetoscroll.challenge.reflection.ReflectionValidator
import com.solvetoscroll.challenge.reflection.ValidationResult
import com.solvetoscroll.challenge.typing.TypingChallengeGenerator
import com.solvetoscroll.challenge.word.WordChallengeGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChallengeUiState(
    val currentChallenge: Challenge? = null,
    val difficulty: Int = 1,
    val attemptCount: Int = 0,
    val waitTimeRemaining: Int = 0,
    val showError: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class ChallengeViewModel @Inject constructor(
    private val challengeSelector: ChallengeSelector,
    private val difficultyManager: DifficultyManager,
    private val waitTimerManager: WaitTimerManager,
    private val mathGenerator: MathChallengeGenerator,
    private val typingGenerator: TypingChallengeGenerator,
    private val reflectionValidator: ReflectionValidator,
    private val memoryGenerator: MemoryChallengeGenerator,
    private val wordGenerator: WordChallengeGenerator
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ChallengeUiState())
    val uiState: StateFlow<ChallengeUiState> = _uiState.asStateFlow()
    
    private var currentPackage: String = ""
    
    fun initialize(packageName: String) {
        currentPackage = packageName
        
        viewModelScope.launch {
            val attemptCount = difficultyManager.getAttemptCount(packageName)
            val difficulty = difficultyManager.getDifficulty(attemptCount)
            
            // Check if wait timer should be active
            val waitTime = waitTimerManager.getRequiredWaitTime(attemptCount)
            if (waitTime.inWholeSeconds > 0 && !waitTimerManager.isWaitActive(packageName)) {
                waitTimerManager.startWait(packageName, attemptCount)
            }
            
            val waitRemaining = waitTimerManager.getRemainingWaitSeconds(packageName)
            
            _uiState.update {
                it.copy(
                    attemptCount = attemptCount,
                    difficulty = difficulty,
                    waitTimeRemaining = waitRemaining
                )
            }
            
            if (waitRemaining > 0) {
                startWaitTimer()
            } else {
                generateNewChallenge()
            }
        }
    }
    
    private fun generateNewChallenge() {
        val challenge = challengeSelector.generateChallenge(_uiState.value.difficulty)
        _uiState.update {
            it.copy(
                currentChallenge = challenge,
                showError = false,
                errorMessage = null
            )
        }
    }
    
    private fun startWaitTimer() {
        viewModelScope.launch {
            while (_uiState.value.waitTimeRemaining > 0) {
                delay(1000)
                val remaining = waitTimerManager.getRemainingWaitSeconds(currentPackage)
                _uiState.update { it.copy(waitTimeRemaining = remaining) }
            }
            
            // Wait is over, generate challenge
            generateNewChallenge()
        }
    }
    
    fun submitMathAnswer(answer: Int?) {
        val challenge = _uiState.value.currentChallenge as? Challenge.Math ?: return
        
        if (answer == challenge.correctAnswer) {
            onChallengeSuccess()
        } else {
            onChallengeFailed()
        }
    }
    
    fun submitTyping(typed: String) {
        val challenge = _uiState.value.currentChallenge as? Challenge.Typing ?: return
        
        if (typingGenerator.validateTyping(challenge.textToType, typed)) {
            onChallengeSuccess()
        } else {
            onChallengeFailed()
        }
    }
    
    fun submitReflection(response: String) {
        val challenge = _uiState.value.currentChallenge as? Challenge.Reflection ?: return
        
        val result = reflectionValidator.validate(response, challenge.minimumWords)
        
        when (result) {
            is ValidationResult.Valid -> onChallengeSuccess()
            is ValidationResult.TooShort -> {
                _uiState.update {
                    it.copy(
                        showError = true,
                        errorMessage = "Write at least ${challenge.minimumWords} words (${result.actual} so far)"
                    )
                }
            }
            is ValidationResult.TooRepetitive -> {
                _uiState.update {
                    it.copy(
                        showError = true,
                        errorMessage = "Please write a more varied response"
                    )
                }
            }
            is ValidationResult.SuspiciousInput -> {
                _uiState.update {
                    it.copy(
                        showError = true,
                        errorMessage = "Please write a meaningful response"
                    )
                }
            }
        }
    }
    
    fun submitMemorySequence(userSequence: List<Int>) {
        val challenge = _uiState.value.currentChallenge as? Challenge.Memory ?: return
        
        if (memoryGenerator.validateSequence(challenge, userSequence)) {
            onChallengeSuccess()
        } else {
            onChallengeFailed()
        }
    }
    
    fun submitWord(answer: String) {
        val challenge = _uiState.value.currentChallenge as? Challenge.Word ?: return
        
        if (wordGenerator.validateAnswer(challenge, answer)) {
            onChallengeSuccess()
        } else {
            onChallengeFailed()
        }
    }
    
    fun onBreathingComplete() {
        // Breathing exercise auto-completes when all cycles are done
        onChallengeSuccess()
    }
    
    private fun onChallengeSuccess() {
        viewModelScope.launch {
            difficultyManager.resetAttempts(currentPackage)
            waitTimerManager.clearWait(currentPackage)
            
            _uiState.update { it.copy(isSuccess = true) }
        }
    }
    
    private fun onChallengeFailed() {
        viewModelScope.launch {
            difficultyManager.incrementAttempt(currentPackage)
            
            val newAttemptCount = difficultyManager.getAttemptCount(currentPackage)
            val newDifficulty = difficultyManager.getDifficulty(newAttemptCount)
            
            // Check if we need to start a wait timer
            val waitTime = waitTimerManager.getRequiredWaitTime(newAttemptCount)
            if (waitTime.inWholeSeconds > 0) {
                waitTimerManager.startWait(currentPackage, newAttemptCount)
            }
            
            val waitRemaining = waitTimerManager.getRemainingWaitSeconds(currentPackage)
            
            _uiState.update {
                it.copy(
                    attemptCount = newAttemptCount,
                    difficulty = newDifficulty,
                    showError = true,
                    errorMessage = "Incorrect. Try again!",
                    waitTimeRemaining = waitRemaining
                )
            }
            
            if (waitRemaining > 0) {
                startWaitTimer()
            } else {
                // Generate a new challenge
                delay(500) // Brief delay to show error
                generateNewChallenge()
            }
        }
    }
}
