package com.solvetoscroll.challenge.memory

import com.solvetoscroll.challenge.Challenge
import kotlin.random.Random

/**
 * Generates memory sequence challenges.
 * User must watch a sequence of colored tiles flash, then reproduce the sequence.
 */
class MemoryChallengeGenerator {
    
    companion object {
        // Number of different items/colors available
        const val ITEM_COUNT = 6
        
        // Display times in milliseconds
        private const val BASE_DISPLAY_TIME_MS = 3000L
        private const val FLASH_PER_ITEM_MS = 600L
    }
    
    /**
     * Generates a memory challenge based on difficulty.
     * 
     * Difficulty 1: 4 items
     * Difficulty 2: 5 items
     * Difficulty 3: 6 items
     * Difficulty 4: 8 items
     */
    fun generate(difficulty: Int): Challenge.Memory {
        val sequenceLength = when (difficulty) {
            1 -> 4
            2 -> 5
            3 -> 6
            else -> 8
        }
        
        // Generate random sequence of item indices
        val sequence = (0 until sequenceLength).map {
            Random.nextInt(ITEM_COUNT)
        }
        
        // Calculate display time based on sequence length
        val displayTime = BASE_DISPLAY_TIME_MS + (sequenceLength * FLASH_PER_ITEM_MS)
        
        return Challenge.Memory(
            sequence = sequence,
            displayTimeMs = displayTime,
            difficulty = difficulty
        )
    }
    
    /**
     * Validates if the user's input matches the original sequence.
     */
    fun validateSequence(challenge: Challenge.Memory, userSequence: List<Int>): Boolean {
        return challenge.sequence == userSequence
    }
}
