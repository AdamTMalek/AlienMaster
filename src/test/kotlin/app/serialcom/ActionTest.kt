package app.serialcom

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ActionTest {
    @Test
    fun testToYamlWithNoValue() {
        val expected = "{\n\taction: get\n\tdevice: DST0\n}"

        val actual = Action(ActionType.GET, DeviceType.DISTANCE_SENSOR, 0, null).toYaml()

        assertEquals(expected, actual)
    }

    @Test
    fun testToYamlWithValue() {
        val expected = "{\n\taction: set\n\tdevice: LED5\n\tvalue: 1\n}"

        val actual = Action(ActionType.SET, DeviceType.LED, 5, 1).toYaml()

        assertEquals(expected, actual)
    }
}