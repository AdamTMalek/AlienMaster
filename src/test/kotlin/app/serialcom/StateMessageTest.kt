package app.serialcom

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StateMessageTest {
    @Test
    fun testToYamlNoValue() {
        val expected = "{\n\tstate: ${State.WAITING.code}\n}"

        val actual = StateMessage(State.WAITING, null).toYaml()

        assertEquals(expected, actual)
    }

    @Test
    fun testToYamlWithValue() {
        val expected = "{\n\tstate: ${State.WAITING.code}\n\tvalue: 15\n}"

        val actual = StateMessage(State.WAITING, 15).toYaml()

        assertEquals(expected, actual)
    }
}