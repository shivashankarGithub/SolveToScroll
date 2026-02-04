package com.solvetoscroll.challenge

import com.solvetoscroll.challenge.breathing.BreathingChallengeGenerator
import com.solvetoscroll.challenge.math.MathChallengeGenerator
import com.solvetoscroll.challenge.memory.MemoryChallengeGenerator
import com.solvetoscroll.challenge.reflection.ReflectionChallengeGenerator
import com.solvetoscroll.challenge.typing.TypingChallengeGenerator
import com.solvetoscroll.challenge.word.WordChallengeGenerator
import kotlin.random.Random
import javax.inject.Inject

/**
 * Selects and generates challenges based on weighted random selection.
 * 
 * Weights:
 * - Math: 20%
 * - Typing: 10%
 * - Reflection: 10%
 * - Memory: 25%
 * - Word: 20%
 * - Breathing: 15%
 */
class ChallengeSelector @Inject constructor(
    private val mathGenerator: MathChallengeGenerator,
    private val typingGenerator: TypingChallengeGenerator,
    private val reflectionGenerator: ReflectionChallengeGenerator,
    private val memoryGenerator: MemoryChallengeGenerator,
    private val wordGenerator: WordChallengeGenerator,
    private val breathingGenerator: BreathingChallengeGenerator
) {
    
    companion object {
        private const val MATH_WEIGHT = 20
        private const val TYPING_WEIGHT = 10
        private const val REFLECTION_WEIGHT = 10
        private const val MEMORY_WEIGHT = 25
        private const val WORD_WEIGHT = 20
        private const val BREATHING_WEIGHT = 15
    }
    
    /**
     * Generates a random challenge at the specified difficulty level.
     * Challenge type is selected based on weighted probabilities.
     */
    fun generateChallenge(difficulty: Int): Challenge {
        val roll = Random.nextInt(100)
        
        var cumulative = 0
        val type = when {
            roll < (cumulative + MATH_WEIGHT).also { cumulative += MATH_WEIGHT } -> ChallengeType.MATH
            roll < (cumulative + TYPING_WEIGHT).also { cumulative += TYPING_WEIGHT } -> ChallengeType.TYPING
            roll < (cumulative + REFLECTION_WEIGHT).also { cumulative += REFLECTION_WEIGHT } -> ChallengeType.REFLECTION
            roll < (cumulative + MEMORY_WEIGHT).also { cumulative += MEMORY_WEIGHT } -> ChallengeType.MEMORY
            roll < (cumulative + WORD_WEIGHT).also { cumulative += WORD_WEIGHT } -> ChallengeType.WORD
            else -> ChallengeType.BREATHING
        }
        
        return generateChallengeOfType(type, difficulty)
    }
    
    /**
     * Generates a challenge of a specific type at the specified difficulty.
     */
    fun generateChallengeOfType(type: ChallengeType, difficulty: Int): Challenge {
        val clampedDifficulty = difficulty.coerceIn(1, 4)
        
        return when (type) {
            ChallengeType.MATH -> mathGenerator.generate(clampedDifficulty)
            ChallengeType.TYPING -> typingGenerator.generate(clampedDifficulty)
            ChallengeType.REFLECTION -> reflectionGenerator.generate(clampedDifficulty)
            ChallengeType.MEMORY -> memoryGenerator.generate(clampedDifficulty)
            ChallengeType.WORD -> wordGenerator.generate(clampedDifficulty)
            ChallengeType.BREATHING -> breathingGenerator.generate(clampedDifficulty)
        }
    }
    
    /**
     * Gets the type of a challenge.
     */
    fun getChallengeType(challenge: Challenge): ChallengeType {
        return when (challenge) {
            is Challenge.Math -> ChallengeType.MATH
            is Challenge.Typing -> ChallengeType.TYPING
            is Challenge.Reflection -> ChallengeType.REFLECTION
            is Challenge.Memory -> ChallengeType.MEMORY
            is Challenge.Word -> ChallengeType.WORD
            is Challenge.Breathing -> ChallengeType.BREATHING
        }
    }
}
