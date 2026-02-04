package com.solvetoscroll.challenge.typing

import com.solvetoscroll.challenge.Challenge
import kotlin.random.Random

/**
 * Generates typing challenges at various difficulty levels.
 * Phrases are focus and productivity themed.
 */
class TypingChallengeGenerator {
    
    private val level1Phrases = listOf(
        "The quick brown fox jumps",
        "Stay focused and be present",
        "Your time is valuable",
        "Choose your actions wisely",
        "Break the scroll habit now",
        "Focus on what matters most",
        "Be intentional with your time",
        "Your attention is precious",
        "Take control of your day",
        "Every moment counts"
    )
    
    private val level2Phrases = listOf(
        "Patience is a virtue that leads to success",
        "Every moment of resistance makes you stronger",
        "What you do today shapes who you become tomorrow",
        "Discipline is choosing what you want most over what you want now",
        "The secret of getting ahead is getting started today",
        "Small daily improvements lead to stunning long-term results",
        "Your future self will thank you for the choices you make now",
        "Consistency is more important than perfection in building habits"
    )
    
    private val level3Phrases = listOf(
        "Before you scroll mindlessly, ask yourself: Is this worth my time?",
        "The best way to predict your future is to create it, not consume it.",
        "Your attention is your most valuable resource. Spend it wisely today.",
        "The difference between who you are and who you want to be is what you do.",
        "Success is the sum of small efforts repeated day in and day out, consistently.",
        "You will never change your life until you change something you do daily."
    )
    
    private val level4Phrases = listOf(
        "In 2024, the average person spends 4+ hours daily on social media. Be different!",
        "Step 1: Put down your phone. Step 2: Take a deep breath. Step 3: Do something real.",
        "Ask yourself: Will I remember this scroll session in 5 years? Probably not. Act accordingly!",
        "The chains of habit are too light to be felt until they are too heavy to be broken. Break them now.",
        "Your phone is a tool, not a pacifier. Use it with intention, not out of boredom or anxiety.",
        "Time is the most valuable thing you can spend. Don't waste it on endless scrolling and notifications."
    )
    
    fun generate(difficulty: Int): Challenge.Typing {
        val phrases = when (difficulty) {
            1 -> level1Phrases
            2 -> level2Phrases
            3 -> level3Phrases
            else -> level4Phrases
        }
        
        return Challenge.Typing(
            textToType = phrases[Random.nextInt(phrases.size)],
            difficulty = difficulty.coerceIn(1, 4)
        )
    }
    
    /**
     * Validates the typed text against the expected text.
     * Returns true only if they match exactly (case-sensitive).
     */
    fun validateTyping(expected: String, actual: String): Boolean {
        return expected == actual
    }
    
    /**
     * Returns character-by-character matching information.
     * Each boolean indicates if the character at that position matches.
     */
    fun getCharacterMatches(expected: String, actual: String): List<Boolean> {
        return actual.mapIndexed { index, char ->
            index < expected.length && expected[index] == char
        }
    }
    
    /**
     * Returns the number of correct characters typed so far.
     */
    fun getCorrectCharacterCount(expected: String, actual: String): Int {
        var count = 0
        for (i in actual.indices) {
            if (i < expected.length && expected[i] == actual[i]) {
                count++
            } else {
                break // Stop counting at first error
            }
        }
        return count
    }
}
