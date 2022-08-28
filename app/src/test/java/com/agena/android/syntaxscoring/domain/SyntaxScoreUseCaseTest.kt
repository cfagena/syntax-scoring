package com.agena.android.syntaxscoring.domain

import com.agena.android.syntaxscoring.entity.CharacterType
import com.agena.android.syntaxscoring.entity.LineResult
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.Exception
import java.util.Stack

class SyntaxScoreUseCaseTest {

    private val subject = SyntaxScoreUseCase()

    @Test
    fun `test if points to closing character returns correct value`() {
        assertEquals(1, subject.getPointsForClosingCharacter('('))
        assertEquals(2, subject.getPointsForClosingCharacter('['))
        assertEquals(3, subject.getPointsForClosingCharacter('{'))
        assertEquals(4, subject.getPointsForClosingCharacter('<'))
    }

    @Test
    fun `test if points for invalid closing character returns correct value`() {
        assertEquals(3, subject.getPointsForInvalidClosingCharacter(')'))
        assertEquals(57, subject.getPointsForInvalidClosingCharacter(']'))
        assertEquals(1197, subject.getPointsForInvalidClosingCharacter('}'))
        assertEquals(25137, subject.getPointsForInvalidClosingCharacter('>'))

        assertEquals(3, subject.getPointsForInvalidClosingCharacter(')'))
        assertEquals(57, subject.getPointsForInvalidClosingCharacter(']'))
        assertEquals(1197, subject.getPointsForInvalidClosingCharacter('}'))
        assertEquals(25137, subject.getPointsForInvalidClosingCharacter('>'))
    }

    @Test(expected = Exception::class)
    fun `given unexpected character test if getClosingCharacter throws exception`() {
        subject.getClosingCharacter('a')
    }

    @Test
    fun `given opening character test if getClosingCharacter returns correctly`() {
        assertEquals(')', subject.getClosingCharacter('('))
        assertEquals(']', subject.getClosingCharacter('['))
        assertEquals('}', subject.getClosingCharacter('{'))
        assertEquals('>', subject.getClosingCharacter('<'))
    }

    @Test
    fun `given valid characters test if classifyCharacter returns correctly`() {
        assertEquals(CharacterType.OPENING, subject.classifyCharacter('('))
        assertEquals(CharacterType.OPENING, subject.classifyCharacter('['))
        assertEquals(CharacterType.OPENING, subject.classifyCharacter('{'))
        assertEquals(CharacterType.OPENING, subject.classifyCharacter('<'))

        assertEquals(CharacterType.CLOSING, subject.classifyCharacter(')'))
        assertEquals(CharacterType.CLOSING, subject.classifyCharacter(']'))
        assertEquals(CharacterType.CLOSING, subject.classifyCharacter('}'))
        assertEquals(CharacterType.CLOSING, subject.classifyCharacter('>'))

        assertEquals(CharacterType.INVALID, subject.classifyCharacter('A'))
    }

    @Test
    fun `given example line test if processLineCompletion returns correct score`() {
        val exampleStack = Stack<Char>().also {
            it.push('<')
            it.push('{')
            it.push('(')
            it.push('[')
        }
        assertEquals(294, subject.processLineCompletion(exampleStack))
    }

    @Test
    fun `given example line test if processLine returns correct result`() {
        val exampleLine = "{([(<{}[<>[]}>{[]{[(<()>"
        val lineResult = subject.processLine(exampleLine, 1)
        assertEquals(LineResult.CORRUPTED, lineResult.first)
        assertEquals(1197, lineResult.second)
    }
}

