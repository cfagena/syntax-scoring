package com.agena.android.syntaxscoring

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import java.util.EmptyStackException
import java.util.Stack

class MainViewModel : ViewModel() {

    companion object {
        private const val TAG: String = "MainViewModel"
    }

    private val _input = MutableLiveData("")
    val input: LiveData<String> = _input

    fun setInput(value: String) {
        _input.value = value
    }

    fun process(): Flow<String> {
        val subsystem = input.value
        var score = 0

        return flow {
            subsystem?.let {
                if (it.isNotBlank()) {
                    it.lines().forEachIndexed { index, line ->
                        emit("Processing line: $index")

                        val lineResult = processLine(line, index)
                        emit("Line $index is ${lineResult.first}")

                        if (LineResult.CORRUPTED == lineResult.first) {
                            score += lineResult.second
                        }
                    }
                }
            }
            emit("Total syntax error score: $score")
        }
    }

    private fun processLine(line: String, lineIndex: Int): Pair<LineResult, Int> {
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
                    // If it is a closing character check if it matches if last pushed character
                    // on the stack
                    try {
                        val poppedChar = lineStack.pop()
                        if (notMatchToOpeningCharacter(poppedChar, character)) {
                            Log.d(
                                TAG,
                                "Corrupted line. Expected ${closingCharacter(poppedChar)}, " +
                                    "but found $character instead at $lineIndex:$charIndex"
                            )
                            return Pair(LineResult.CORRUPTED, getPointsForCharacter(character))
                        }
                    } catch (e: EmptyStackException) {
                        Log.d(
                            TAG,
                            "No opening character for: [$character, $lineIndex:$charIndex]"
                        )
                        return Pair(LineResult.CORRUPTED, getPointsForCharacter(character))
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
            Pair(LineResult.INCOMPLETE, 0)

        return Pair(LineResult.COMPLETE, 0)
    }

    private fun classifyCharacter(character: Char): CharacterType {
        return when (character) {
            '(', '[', '{', '<' -> CharacterType.OPENING
            ')', ']', '}', '>' -> CharacterType.CLOSING
            else -> CharacterType.INVALID
        }
    }

    private fun notMatchToOpeningCharacter(openingCharacter: Char, closingCharacter: Char): Boolean {
        return closingCharacter(openingCharacter) != closingCharacter
    }

    private fun closingCharacter(openingCharacter: Char): Char {
        return when (openingCharacter) {
            '(' -> ')'
            '[' -> ']'
            '{' -> '}'
            '<' -> '>'
            else -> throw Exception("Invalid State, this character should not be here")
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

enum class LineResult {
    CORRUPTED,
    INCOMPLETE,
    COMPLETE
}

enum class CharacterType {
    OPENING,
    CLOSING,
    INVALID
}
