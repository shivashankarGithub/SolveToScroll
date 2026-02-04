package com.solvetoscroll.challenge.reflection

/**
 * Validates reflection responses to detect gaming attempts.
 */
class ReflectionValidator {
    
    private val suspiciousPatterns = listOf(
        "asdf", "qwer", "zxcv", "jkl", "aaa", "bbb", "ccc", "ddd",
        "xxx", "yyy", "zzz", "123", "abc", "test", "blah"
    )
    
    fun validate(response: String, minimumWords: Int): ValidationResult {
        val trimmed = response.trim()
        
        // Check if response is too short (character count)
        if (trimmed.length < 10) {
            return ValidationResult.TooShort(0, minimumWords)
        }
        
        // Split into words
        val words = trimmed.split("\\s+".toRegex()).filter { it.isNotBlank() }
        val wordCount = words.size
        
        // Check word count
        if (wordCount < minimumWords) {
            return ValidationResult.TooShort(wordCount, minimumWords)
        }
        
        // Check for repetitive content (same word repeated too many times)
        val uniqueWords = words.map { it.lowercase() }.toSet()
        val uniqueRatio = uniqueWords.size.toFloat() / wordCount
        if (uniqueRatio < 0.4f) {
            return ValidationResult.TooRepetitive
        }
        
        // Check for keyboard mashing patterns
        val lowerResponse = trimmed.lowercase()
        if (suspiciousPatterns.any { lowerResponse.contains(it) }) {
            return ValidationResult.SuspiciousInput
        }
        
        // Check for very short average word length (gibberish detection)
        val avgWordLength = words.sumOf { it.length } / wordCount.toFloat()
        if (avgWordLength < 2.5f) {
            return ValidationResult.SuspiciousInput
        }
        
        // Check for too many consecutive same characters
        if (hasExcessiveRepeatingChars(trimmed)) {
            return ValidationResult.SuspiciousInput
        }
        
        return ValidationResult.Valid
    }
    
    private fun hasExcessiveRepeatingChars(text: String): Boolean {
        var consecutiveCount = 1
        var lastChar = ' '
        
        for (char in text) {
            if (char == lastChar && char.isLetter()) {
                consecutiveCount++
                if (consecutiveCount >= 4) {
                    return true
                }
            } else {
                consecutiveCount = 1
            }
            lastChar = char
        }
        
        return false
    }
    
    /**
     * Count words in a string
     */
    fun countWords(text: String): Int {
        return text.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size
    }
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class TooShort(val actual: Int, val required: Int) : ValidationResult()
    object TooRepetitive : ValidationResult()
    object SuspiciousInput : ValidationResult()
}
