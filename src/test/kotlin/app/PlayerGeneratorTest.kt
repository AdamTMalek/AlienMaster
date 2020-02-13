package app

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PlayerGeneratorTest {
    @Test
    fun throwsIllegalArgException() {
        assertThrows<IllegalArgumentException> {
            PlayerGenerator().generatePlayers(0, false)
        }
    }

    @Test
    fun testGeneratesFivePlayers() {
        val expectedSize = 5
        val players = PlayerGenerator().generatePlayers(expectedSize, false)

        assertEquals(expectedSize, players.size)
    }

    @Test
    fun testGeneratesSixteenPlayers() {
        val expectedSize = 16
        val players = PlayerGenerator().generatePlayers(expectedSize, false)

        assertEquals(expectedSize, players.size)
    }

    @Test
    fun testAllScoresAreZero() {
        val players = PlayerGenerator().generatePlayers(10, false)

        assertTrue(players.all { player -> player.score == 0 })
    }

    @Test
    fun testScoreIsRandom() {
        val players = PlayerGenerator().generatePlayers(10, true)

        assertFalse(players.all { player -> player.score == 0 })
    }
}