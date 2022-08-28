package com.agena.android.syntaxscoring.domain

import android.util.Log
import com.agena.android.syntaxscoring.entity.CharacterType
import com.agena.android.syntaxscoring.entity.LineInfo
import com.agena.android.syntaxscoring.entity.LineResult
import java.lang.Exception
import java.util.EmptyStackException
import java.util.Stack

class RemoveCorruptedLinesUseCase {

    companion object {
        private const val TAG: String = "RemoveCorruptedLinesUseCase"
    }

    operator fun invoke(input: List<String>): Pair<Long, List<String>> {
        var score = 0L
        val resultList = mutableListOf<String>()

        if (input.isNotEmpty()) {
            input.forEachIndexed { index, line ->
                Log.d(TAG, "Processing line: $index")

                val lineResult = processLine(line, index)
                Log.d(TAG, "Line $index is ${lineResult.result}")

                if (LineResult.CORRUPTED == lineResult.result) {
                    score += lineResult.score
                    Log.d(TAG, lineResult.remark ?: "")
                } else if (LineResult.INCOMPLETE == lineResult.result) {
                    resultList.add(line)
                }
            }
        }

        return Pair(score, resultList)
    }

    private fun processLine(line: String, lineIndex: Int): LineInfo {
        if (line.isBlank())
            return LineInfo(LineResult.COMPLETE, 0)

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
                            val message = "Corrupted line. Expected " +
                                "${getClosingCharacter(poppedChar)}, " +
                                "but found $character instead at $lineIndex:$charIndex"

                            return LineInfo(
                                LineResult.CORRUPTED, getPointsForCharacter(character), message
                            )
                        }
                    } catch (e: EmptyStackException) {
                        val message = "No opening character for: " +
                            "[$character, $lineIndex:$charIndex]"
                        return LineInfo(
                            LineResult.CORRUPTED, getPointsForCharacter(character),
                            message
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
        if (!lineStack.empty())
            return LineInfo(LineResult.INCOMPLETE, 0)

        return LineInfo(LineResult.COMPLETE, 0)
    }

    private fun classifyCharacter(character: Char): CharacterType {
        return when (character) {
            '(', '[', '{', '<' -> CharacterType.OPENING
            ')', ']', '}', '>' -> CharacterType.CLOSING
            else -> CharacterType.INVALID
        }
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

    private fun getPointsForCharacter(character: Char): Int {
        return when (character) {
            ')' -> 3
            ']' -> 57
            '}' -> 1197
            '>' -> 25137
            else -> throw Exception("Invalid State, this character should not be here")
        }
    }
}
