package app.serialcom

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ActionTest {
    @Test
    fun testToYamlWithNoValue() {
        val expected = "{\n\taction: get\n\tdevice: DST0\n}"

        val actual = Action(ActionType.GET, DeviceType.DISTANCE_SENSOR, 0, emptyList()).toYaml()

        assertEquals(expected, actual)
    }

    @Test
    fun testToYamlWithSingleValue() {
        val expected = "{\n\taction: set\n\tdevice: LED5\n\tvalue: 1\n}"

        val actual = Action(ActionType.SET, DeviceType.LED, 5, listOf(1)).toYaml()

        assertEquals(expected, actual)
    }

    @Test
    fun testToYamlWithMultipleValues() {
        val expected = "{\n\taction: report\n\tdevice: TCS0\n\tvalue: 1, 2, 3\n}"

        val actual = Action(ActionType.REPORT, DeviceType.COLOUR_SENSOR, 0, listOf(1, 2, 3)).toYaml()

        assertEquals(expected, actual)
    }
}