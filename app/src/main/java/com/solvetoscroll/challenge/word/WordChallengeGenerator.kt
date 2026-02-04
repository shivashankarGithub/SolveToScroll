package com.solvetoscroll.challenge.word

import com.solvetoscroll.challenge.Challenge
import kotlin.random.Random

/**
 * Generates word puzzle (anagram) challenges.
 * User must unscramble jumbled letters to form the correct word.
 */
class WordChallengeGenerator {
    
    companion object {
        // Focus/productivity themed words organized by length
        private val WORDS_5_LETTERS = listOf(
            "FOCUS", "BRAIN", "THINK", "GOALS", "PEACE",
            "RELAX", "QUIET", "AWARE", "CLEAR", "DREAM",
            "DRIVE", "FRESH", "POWER", "SPARK", "TRUTH",
            "VALUE", "WORTH", "LIGHT", "SMILE", "TRUST"
        )
        
        private val WORDS_6_LETTERS = listOf(
            "MENTAL", "BREATH", "GROWTH", "INTENT", "DESIRE",
            "EFFORT", "ENERGY", "HEALTH", "CHANGE", "WISDOM",
            "REASON", "SPIRIT", "CHOICE", "ACTION", "WONDER",
            "LISTEN", "VISION", "MOMENT", "HUMBLE", "HONEST"
        )
        
        private val WORDS_7_LETTERS = listOf(
            "MINDFUL", "BALANCE", "CLARITY", "HEALING", "PURPOSE",
            "COURAGE", "FREEDOM", "MORNING", "PATIENT", "PRESENT",
            "REFLECT", "BELIEVE", "ACHIEVE", "INSPIRE", "CONNECT",
            "EMBRACE", "GENUINE", "THOUGHT", "IMPROVE", "JOURNEY"
        )
        
        private val WORDS_8_PLUS_LETTERS = listOf(
            "AWARENESS", "GRATITUDE", "INTENTION", "RESILIENT",
            "TRANSFORM", "BREATHING", "STILLNESS", "POTENTIAL",
            "DEDICATED", "CONFIDENT", "MOTIVATED", "EMPOWERED",
            "MEDITATE", "PROGRESS", "STRENGTH", "OPTIMIZE",
            "POSITIVE", "CREATIVE", "PATIENCE", "SERENITY"
        )
    }
    
    /**
     * Generates a word challenge based on difficulty.
     * 
     * Difficulty 1: 5-letter words
     * Difficulty 2: 6-letter words
     * Difficulty 3: 7-letter words
     * Difficulty 4: 8+ letter words
     */
    fun generate(difficulty: Int): Challenge.Word {
        val wordList = when (difficulty) {
            1 -> WORDS_5_LETTERS
            2 -> WORDS_6_LETTERS
            3 -> WORDS_7_LETTERS
            else -> WORDS_8_PLUS_LETTERS
        }
        
        val originalWord = wordList.random()
        val scrambledWord = scrambleWord(originalWord)
        
        return Challenge.Word(
            originalWord = originalWord,
            scrambledWord = scrambledWord,
            difficulty = difficulty
        )
    }
    
    /**
     * Scrambles a word, ensuring the result is different from the original.
     */
    private fun scrambleWord(word: String): String {
        var scrambled: String
        var attempts = 0
        
        do {
            val chars = word.toMutableList()
            // Fisher-Yates shuffle
            for (i in chars.size - 1 downTo 1) {
                val j = Random.nextInt(i + 1)
                val temp = chars[i]
                chars[i] = chars[j]
                chars[j] = temp
            }
            scrambled = chars.joinToString("")
            attempts++
        } while (scrambled == word && attempts < 10)
        
        // If somehow still the same (rare for long words), swap first two chars
        if (scrambled == word && word.length >= 2) {
            scrambled = word[1].toString() + word[0] + word.substring(2)
        }
        
        return scrambled
    }
    
    /**
     * Validates if the user's answer matches the original word (case insensitive).
     */
    fun validateAnswer(challenge: Challenge.Word, userAnswer: String): Boolean {
        return challenge.originalWord.equals(userAnswer.trim(), ignoreCase = true)
    }
}
