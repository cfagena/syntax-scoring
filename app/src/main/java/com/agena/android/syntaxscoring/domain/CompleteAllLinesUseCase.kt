package com.agena.android.syntaxscoring.domain

import android.util.Log
import com.agena.android.syntaxscoring.entity.CharacterType
import com.agena.android.syntaxscoring.entity.LineResult
import java.lang.Exception
import java.util.EmptyStackException
import java.util.Stack

class CompleteAllLinesUseCase {

    companion object {
        private const val TAG: String = "CompleteAllLinesUseCase"
    }

    operator fun invoke(input: List<String>): Long {
        val scores = mutableListOf<Long>()
        input.ifEmpty {
            loadExample()
        }.let {
            it.forEachIndexed { lineIndex, line ->
                val lineInfo = processLine(line, lineIndex)
                if (LineResult.INCOMPLETE == lineInfo.first)
                    scores.add(lineInfo.second)
            }
        }

        return scores.sortedBy { it }[scores.size / 2]
    }

    private fun processLine(line: String, lineIndex: Int): Pair<LineResult, Long> {
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
                            Log.w(TAG, "Corrupted line: $lineIndex, ignoring it")
                            return Pair(LineResult.CORRUPTED, 0)
                        }
                    } catch (e: EmptyStackException) {
                        Log.w(TAG, "Corrupted line: $lineIndex, ignoring it")
                        return Pair(LineResult.CORRUPTED, 0)
                    }
                }

                CharacterType.INVALID -> {
                    "Invalid character: [$character, $lineIndex:$charIndex], ignoring it".let {
                        Log.w(TAG, it)
                    }
                }
            }
        }
        if (!lineStack.empty()) {
            val completionPoints = processLineCompletion(lineStack)
            return Pair(LineResult.INCOMPLETE, completionPoints)
        }

        return Pair(LineResult.COMPLETE, 0)
    }

    private fun processLineCompletion(lineStack: Stack<Char>): Long {
        var score = 0L
        while (lineStack.isNotEmpty()) {
            lineStack.pop()?.let {
                score = 5 * score + getPointsForClosingCharacter(it)
            }
        }
        Log.d(TAG, "Completion score: $score")
        return score
    }

    private fun notMatchToOpeningCharacter(openingCharacter: Char, closingCharacter: Char): Boolean {
        return getClosingCharacter(openingCharacter) != closingCharacter
    }

    private fun getClosingCharacter(openingCharacter: Char): Char {
        return when (openingCharacter) {
            '(' -> ')'
            '[' -> ']'
            '{' -> '}'
            '<' -> '>'
            else -> throw Exception("Input is not a opening character")
        }
    }

    private fun classifyCharacter(character: Char): CharacterType {
        return when (character) {
            '(', '[', '{', '<' -> CharacterType.OPENING
            ')', ']', '}', '>' -> CharacterType.CLOSING
            else -> CharacterType.INVALID
        }
    }

    private fun getPointsForClosingCharacter(openingCharacter: Char): Int {
        return when (openingCharacter) {
            '(' -> 1
            '[' -> 2
            '{' -> 3
            '<' -> 4
            else -> throw Exception("Invalid State, this is not an opening character")
        }
    }

    private fun loadExample(): List<String> {
        return listOf(
            "[({(<(())[]>[[{[]{<()<>>",
            "[(()[<>])]({[<{<<[]>>(",
            "(((({<>}<{<{<>}{[]{[]{}",
            "{<[[]]>}<{[{[{[]{()[[[]",
            "<{([{{}}[<[[[<>{}]]]>[]]"
        )
    }
}
