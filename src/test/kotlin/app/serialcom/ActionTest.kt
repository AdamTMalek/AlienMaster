package app.serialcom

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ActionTest {
    @Test
    fun testToYamlWithNoValue() {
        val expected = """
            action: get
            device: DST0
        """.trimIndent()

        val actual = Action(ActionType.GET, DeviceType.DISTANCE_SENSOR, 0, null).toYaml()

        assertEquals(expected, actual)
    }

    @Test
    fun testToYamlWithValue() {
        val expected = """
            action: set
            device: LED5
            value: 1
        """.trimIndent()

        val actual = Action(ActionType.SET, DeviceType.LED, 5, 1).toYaml()

        assertEquals(expected, actual)
    }
}