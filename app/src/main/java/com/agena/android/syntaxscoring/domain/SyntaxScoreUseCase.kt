package com.agena.android.syntaxscoring.domain

import android.util.Log
import com.agena.android.syntaxscoring.entity.CharacterType
import com.agena.android.syntaxscoring.entity.LineResult
import java.lang.Exception
import java.util.EmptyStackException
import java.util.Stack

class SyntaxScoreUseCase {

    companion object {
        private const val TAG: String = "SyntaxScoreUseCase"
    }

    operator fun invoke(input: List<String>): Pair<Long, Long> {
        var scoreCorruptedLines = 0L
        val scoresLineCompletion = mutableListOf<Long>()

        if (input.isNotEmpty()) {
            input.forEachIndexed { index, line ->
                val lineResult = processLine(line, index)

                if (LineResult.CORRUPTED == lineResult.first) {
                    scoreCorruptedLines += lineResult.second
                } else if (LineResult.INCOMPLETE == lineResult.first) {
                    scoresLineCompletion.add(lineResult.second)
                }
            }
        }

        val result = Pair(
            scoreCorruptedLines,
            scoresLineCompletion.sortedBy { it }[scoresLineCompletion.size / 2]
        )
        Log.d(TAG, "Score part 1: ${result.first}/part 2: ${result.second}")
        return result
    }

    internal fun processLine(line: String, lineIndex: Int): Pair<LineResult, Long> {
        if (line.isBlank())
            return Pair(LineResult.COMPLETE, 0)

        // Stack data structure (LIFO) to store the opening characters
        val lineStack = Stack<Char>()

        line.forEachIndexed { charIndex, character ->
            when (classifyCharacter(character)) {
                CharacterType.OPENING -> {
                    // If opening character push to the stack
                    lineStack.push(character)
                }

                CharacterType.CLOSING -> {
                    // If it is a closing character check if it matches the last pushed character
                    // on the stack
                    try {
                        val poppedChar = lineStack.pop()
                        if (notMatchToOpeningCharacter(poppedChar, character)) {
                            val message = "Line $lineIndex Corrupted. Expected " +
                                "${getClosingCharacter(poppedChar)}, " +
                                "but found $character instead at $lineIndex:$charIndex"
                            Log.d(TAG, message)

                            return Pair(
                                LineResult.CORRUPTED,
                                getPointsForInvalidClosingCharacter(character).toLong()
                            )
                        }
                    } catch (e: EmptyStackException) {
                        val message = "No opening character for: " +
                            "[$character, $lineIndex:$charIndex]"
                        Log.d(TAG, message)
                        return Pair(
                            LineResult.CORRUPTED,
                            getPointsForInvalidClosingCharacter(character).toLong()
                        )
                    }
                }

                CharacterType.INVALID -> {
                    // If it is not expected character just ignoring it
                    Log.w(
                        TAG,
                        "Character invalid: [$character, $lineIndex:$charIndex], ignoring it"
                    )
                }
            }
        }
        if (!lineStack.empty()) {
            val completionPoints = processLineCompletion(lineStack)
            Log.d(TAG, "Line $lineIndex Incomplete. Score: $completionPoints")
            return Pair(LineResult.INCOMPLETE, completionPoints)
        }

        return Pair(LineResult.COMPLETE, 0)
    }

    internal fun processLineCompletion(lineStack: Stack<Char>): Long {
        var score = 0L
        while (lineStack.isNotEmpty()) {
            lineStack.pop()?.let {
                score = 5 * score + getPointsForClosingCharacter(it)
            }
        }

        return score
    }

    internal fun classifyCharacter(character: Char): CharacterType {
        return when (character) {
            '(', '[', '{', '<' -> CharacterType.OPENING
            ')', ']', '}', '>' -> CharacterType.CLOSING
            else -> CharacterType.INVALID
        }
    }

    private fun notMatchToOpeningCharacter(openingCharacter: Char, closingCharacter: Char): Boolean {
        return getClosingCharacter(openingCharacter) != closingCharacter
    }

    internal fun getClosingCharacter(openingCharacter: Char): Char {
        return when (openingCharacter) {
            '(' -> ')'
            '[' -> ']'
            '{' -> '}'
            '<' -> '>'
            else -> throw Exception("Input is not a opening character")
        }
    }

    internal fun getPointsForInvalidClosingCharacter(character: Char): Int {
        return when (character) {
            ')' -> 3
            ']' -> 57
            '}' -> 1197
            '>' -> 25137
            else -> throw Exception("Invalid State, this character should not be here")
        }
    }

    internal fun getPointsForClosingCharacter(openingCharacter: Char): Int {
        return when (openingCharacter) {
            '(' -> 1
            '[' -> 2
            '{' -> 3
            '<' -> 4
            else -> throw Exception("Invalid State, this is not an opening character")
        }
    }
}
