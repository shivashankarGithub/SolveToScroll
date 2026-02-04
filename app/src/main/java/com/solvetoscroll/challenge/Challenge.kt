package com.solvetoscroll.challenge

/**
 * Sealed class representing all challenge types
 */
sealed class Challenge {
    abstract val difficulty: Int
    
    data class Math(
        val displayText: String,
        val questionPrompt: String,
        val correctAnswer: Int,
        override val difficulty: Int
    ) : Challenge()
    
    data class Typing(
        val textToType: String,
        override val difficulty: Int
    ) : Challenge()
    
    data class Reflection(
        val prompt: String,
        val minimumWords: Int,
        override val difficulty: Int
    ) : Challenge()
    
    /**
     * Memory sequence challenge - user must remember and reproduce a sequence
     */
    data class Memory(
        val sequence: List<Int>,  // Indices of items to remember (0-5 for 6 colors/items)
        val displayTimeMs: Long,  // How long to show the sequence
        override val difficulty: Int
    ) : Challenge()
    
    /**
     * Word puzzle challenge - user must unscramble an anagram
     */
    data class Word(
        val originalWord: String,     // The correct answer
        val scrambledWord: String,    // The jumbled letters shown to user
        override val difficulty: Int
    ) : Challenge()
    
    /**
     * Breathing exercise challenge - user must complete breathing cycles
     */
    data class Breathing(
        val cycles: Int,              // Number of breath cycles required
        val inhaleSeconds: Int,       // Duration of inhale phase
        val holdSeconds: Int,         // Duration of hold phase
        val exhaleSeconds: Int,       // Duration of exhale phase
        override val difficulty: Int
    ) : Challenge() {
        val totalDurationSeconds: Int
            get() = cycles * (inhaleSeconds + holdSeconds + exhaleSeconds)
    }
}

enum class ChallengeType {
    MATH,
    TYPING,
    REFLECTION,
    MEMORY,
    WORD,
    BREATHING
}
