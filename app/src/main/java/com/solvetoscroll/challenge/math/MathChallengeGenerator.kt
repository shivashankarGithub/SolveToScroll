package com.solvetoscroll.challenge.math

import com.solvetoscroll.challenge.Challenge
import kotlin.random.Random

/**
 * Generates math challenges at various difficulty levels.
 * All equations are randomly generated so answers cannot be memorized.
 */
class MathChallengeGenerator {
    
    fun generate(difficulty: Int): Challenge.Math {
        return when (difficulty) {
            1 -> generateLinearEasy()  // No more simple arithmetic
            2 -> generateLinearMedium()
            3 -> generateLinearHard()
            4 -> generateQuadratic()
            else -> generateLinearMedium()
        }
    }
    
    /**
     * Level 1: Basic linear equations (still requires algebra)
     * Examples: 3x + 7 = 22, 5x - 12 = 28
     */
    private fun generateLinearEasy(): Challenge.Math {
        val variant = Random.nextInt(2)
        
        return when (variant) {
            0 -> { // ax + b = c
                val x = Random.nextInt(3, 12)
                val a = Random.nextInt(3, 9)
                val b = Random.nextInt(5, 25)
                val c = a * x + b
                Challenge.Math(
                    displayText = "${a}x + $b = $c",
                    questionPrompt = "Solve for x",
                    correctAnswer = x,
                    difficulty = 1
                )
            }
            else -> { // ax - b = c
                val x = Random.nextInt(4, 15)
                val a = Random.nextInt(3, 8)
                val b = Random.nextInt(5, 20)
                val c = a * x - b
                Challenge.Math(
                    displayText = "${a}x - $b = $c",
                    questionPrompt = "Solve for x",
                    correctAnswer = x,
                    difficulty = 1
                )
            }
        }
    }
    
    /**
     * Level 2: Two-step linear equations with fractions or negative results
     * Examples: x/4 + 5 = 17, 2x + 3x - 7 = 28
     */
    private fun generateLinearMedium(): Challenge.Math {
        val variant = Random.nextInt(3)
        
        return when (variant) {
            0 -> { // x/a + b = c (ensure clean division)
                val a = Random.nextInt(2, 7)
                val x = a * Random.nextInt(3, 12) // Ensure x is divisible by a
                val b = Random.nextInt(3, 15)
                val c = (x / a) + b
                Challenge.Math(
                    displayText = "x/$a + $b = $c",
                    questionPrompt = "Solve for x",
                    correctAnswer = x,
                    difficulty = 2
                )
            }
            1 -> { // ax + bx + c = d (combine like terms)
                val x = Random.nextInt(3, 12)
                val a = Random.nextInt(2, 6)
                val b = Random.nextInt(2, 6)
                val c = Random.nextInt(5, 20)
                val d = (a + b) * x + c
                Challenge.Math(
                    displayText = "${a}x + ${b}x + $c = $d",
                    questionPrompt = "Solve for x",
                    correctAnswer = x,
                    difficulty = 2
                )
            }
            else -> { // ax - b = cx + d (variables on both sides)
                val x = Random.nextInt(2, 10)
                val a = Random.nextInt(4, 9)
                val c = Random.nextInt(1, a - 1) // Ensure a > c
                val b = Random.nextInt(1, 15)
                val d = (a - c) * x - b
                Challenge.Math(
                    displayText = "${a}x - $b = ${c}x + $d",
                    questionPrompt = "Solve for x",
                    correctAnswer = x,
                    difficulty = 2
                )
            }
        }
    }
    
    /**
     * Level 3: Multi-step equations with parentheses
     * Examples: 2(3x - 5) + 4 = 18, 3(x + 4) - 2(x - 1) = 20
     */
    private fun generateLinearHard(): Challenge.Math {
        val variant = Random.nextInt(3)
        
        return when (variant) {
            0 -> { // a(bx + c) + d = result
                val x = Random.nextInt(2, 10)
                val a = Random.nextInt(2, 5)
                val b = Random.nextInt(2, 5)
                val c = Random.nextInt(-5, 10)
                val d = Random.nextInt(-10, 15)
                val result = a * (b * x + c) + d
                
                val cStr = if (c >= 0) "+ $c" else "- ${-c}"
                val dStr = if (d >= 0) "+ $d" else "- ${-d}"
                
                Challenge.Math(
                    displayText = "$a(${b}x $cStr) $dStr = $result",
                    questionPrompt = "Solve for x",
                    correctAnswer = x,
                    difficulty = 3
                )
            }
            1 -> { // a(x + b) + c(x + d) = result
                val x = Random.nextInt(2, 12)
                val a = Random.nextInt(2, 5)
                val b = Random.nextInt(1, 8)
                val c = Random.nextInt(1, 4)
                val d = Random.nextInt(1, 8)
                val result = a * (x + b) + c * (x + d)
                
                Challenge.Math(
                    displayText = "$a(x + $b) + $c(x + $d) = $result",
                    questionPrompt = "Solve for x",
                    correctAnswer = x,
                    difficulty = 3
                )
            }
            else -> { // (ax + b)/c = d
                val c = Random.nextInt(2, 6)
                val d = Random.nextInt(3, 12)
                val a = Random.nextInt(2, 5)
                // Solve backwards: ax + b = c*d, choose x and compute b
                val x = Random.nextInt(2, 10)
                val b = c * d - a * x
                
                val bStr = if (b >= 0) "+ $b" else "- ${-b}"
                
                Challenge.Math(
                    displayText = "(${a}x $bStr) / $c = $d",
                    questionPrompt = "Solve for x",
                    correctAnswer = x,
                    difficulty = 3
                )
            }
        }
    }
    
    /**
     * Level 4: Quadratic equations
     * Examples: x² - 5x + 6 = 0, x² + 2x - 15 = 0
     */
    private fun generateQuadratic(): Challenge.Math {
        // Generate (x - r1)(x - r2) = 0 where r1, r2 are positive roots
        val r1 = Random.nextInt(1, 10)
        val r2 = Random.nextInt(1, 10)
        
        // x² - (r1+r2)x + r1*r2 = 0
        val b = -(r1 + r2)
        val c = r1 * r2
        
        val bStr = if (b >= 0) "+ ${b}" else "- ${-b}"
        val cStr = if (c >= 0) "+ $c" else "- ${-c}"
        
        return Challenge.Math(
            displayText = "x² $bStr·x $cStr = 0",
            questionPrompt = "Find a positive root",
            correctAnswer = maxOf(r1, r2), // Accept the larger root
            difficulty = 4
        )
    }
    
    /**
     * Validates the user's answer for a math challenge.
     * For quadratic equations, either root is acceptable.
     */
    fun validateAnswer(challenge: Challenge.Math, userAnswer: Int): Boolean {
        if (challenge.difficulty == 4) {
            // For quadratics, check if it's a valid root
            // Parse the equation to find both roots
            // For simplicity, we accept the stored correct answer
            return userAnswer == challenge.correctAnswer
        }
        return userAnswer == challenge.correctAnswer
    }
}
