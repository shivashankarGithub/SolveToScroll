package com.solvetoscroll.challenge.breathing

import com.solvetoscroll.challenge.Challenge

/**
 * Generates breathing exercise challenges.
 * User must complete a set number of breathing cycles.
 * This challenge cannot be rushed - forces a mandatory pause.
 */
class BreathingChallengeGenerator {
    
    companion object {
        // Standard 4-4-4 breathing pattern (box breathing)
        private const val DEFAULT_INHALE_SECONDS = 4
        private const val DEFAULT_HOLD_SECONDS = 4
        private const val DEFAULT_EXHALE_SECONDS = 4
    }
    
    /**
     * Generates a breathing challenge based on difficulty.
     * 
     * Difficulty 1: 2 cycles (24 seconds total)
     * Difficulty 2: 3 cycles (36 seconds total)
     * Difficulty 3: 3 cycles (36 seconds total)
     * Difficulty 4: 4 cycles (48 seconds total)
     */
    fun generate(difficulty: Int): Challenge.Breathing {
        val cycles = when (difficulty) {
            1 -> 2
            2 -> 3
            3 -> 3
            else -> 4
        }
        
        return Challenge.Breathing(
            cycles = cycles,
            inhaleSeconds = DEFAULT_INHALE_SECONDS,
            holdSeconds = DEFAULT_HOLD_SECONDS,
            exhaleSeconds = DEFAULT_EXHALE_SECONDS,
            difficulty = difficulty
        )
    }
}

/**
 * Represents the current phase in a breathing cycle.
 */
enum class BreathingPhase {
    INHALE,
    HOLD,
    EXHALE,
    COMPLETE
}
