package com.solvetoscroll.challenge.reflection

import com.solvetoscroll.challenge.Challenge
import kotlin.random.Random

/**
 * Generates reflection challenges that require thoughtful written responses.
 */
class ReflectionChallengeGenerator {
    
    private val level1Prompts = listOf(
        Challenge.Reflection(
            prompt = "Why do you want to open this app right now?",
            minimumWords = 15,
            difficulty = 1
        ),
        Challenge.Reflection(
            prompt = "What do you hope to get from using this app?",
            minimumWords = 15,
            difficulty = 1
        ),
        Challenge.Reflection(
            prompt = "How are you feeling right now, and why did you reach for your phone?",
            minimumWords = 15,
            difficulty = 1
        ),
        Challenge.Reflection(
            prompt = "What would be a better use of the next 10 minutes?",
            minimumWords = 15,
            difficulty = 1
        )
    )
    
    private val level2Prompts = listOf(
        Challenge.Reflection(
            prompt = "What specific thing do you need to do in this app? Be detailed.",
            minimumWords = 20,
            difficulty = 2
        ),
        Challenge.Reflection(
            prompt = "What were you doing before you reached for your phone?",
            minimumWords = 20,
            difficulty = 2
        ),
        Challenge.Reflection(
            prompt = "Describe your current emotional state and why you want this distraction.",
            minimumWords = 20,
            difficulty = 2
        ),
        Challenge.Reflection(
            prompt = "What task or responsibility are you putting off right now?",
            minimumWords = 20,
            difficulty = 2
        )
    )
    
    private val level3Prompts = listOf(
        Challenge.Reflection(
            prompt = "Describe what you're avoiding by opening this app, and what you should be doing instead.",
            minimumWords = 25,
            difficulty = 3
        ),
        Challenge.Reflection(
            prompt = "How will you feel in 30 minutes if you spend that time on this app?",
            minimumWords = 25,
            difficulty = 3
        ),
        Challenge.Reflection(
            prompt = "What is one meaningful thing you could accomplish instead of scrolling?",
            minimumWords = 25,
            difficulty = 3
        ),
        Challenge.Reflection(
            prompt = "Describe how your phone habits have affected your productivity this week.",
            minimumWords = 25,
            difficulty = 3
        )
    )
    
    private val level4Prompts = listOf(
        Challenge.Reflection(
            prompt = "Write about a time this app negatively affected your productivity or mood. What would you do differently?",
            minimumWords = 30,
            difficulty = 4
        ),
        Challenge.Reflection(
            prompt = "Imagine your ideal self watching you right now. What would they say about this choice?",
            minimumWords = 30,
            difficulty = 4
        ),
        Challenge.Reflection(
            prompt = "Describe your relationship with this app. Is it serving you, or are you serving it?",
            minimumWords = 30,
            difficulty = 4
        ),
        Challenge.Reflection(
            prompt = "What would your life look like if you spent half as much time on apps like this one?",
            minimumWords = 30,
            difficulty = 4
        )
    )
    
    fun generate(difficulty: Int): Challenge.Reflection {
        val prompts = when (difficulty) {
            1 -> level1Prompts
            2 -> level2Prompts
            3 -> level3Prompts
            else -> level4Prompts
        }
        
        return prompts[Random.nextInt(prompts.size)]
    }
}
