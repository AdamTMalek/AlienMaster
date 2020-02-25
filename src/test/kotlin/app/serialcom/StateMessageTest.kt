package app.serialcom

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StateMessageTest {
    @Test
    fun testToYamlNoValue() {
        val expected = """
            state: ${State.WAITING.code}
            
        """.trimIndent()

        val actual = StateMessage(State.WAITING, null).toYaml()

        assertEquals(expected, actual)
    }

    @Test
    fun testToYamlWithValue() {
        val expected = """
            state: ${State.WAITING.code}
            value: 15
            
        """.trimIndent()

        val actual = StateMessage(State.WAITING, 15).toYaml()

        assertEquals(expected, actual)
    }
}