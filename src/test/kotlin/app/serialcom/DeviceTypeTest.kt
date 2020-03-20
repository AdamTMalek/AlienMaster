package app.serialcom

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeviceTypeTest {
    @Test
    fun testButtonFromString() {
        val expected = DeviceType.BUTTON
        val actual = DeviceType.fromString("btn")
        assertEquals(expected, actual)
    }

    @Test
    fun testLedFromString() {
        val expected = DeviceType.LED
        val actual = DeviceType.fromString("led")
        assertEquals(expected, actual)
    }

    @Test
    fun testServoFromString() {
        val expected = DeviceType.SERVO
        val actual = DeviceType.fromString("srv")
        assertEquals(expected, actual)
    }

    @Test
    fun testDistanceSensorFromString() {
        val expected = DeviceType.DISTANCE_SENSOR
        val actual = DeviceType.fromString("dst")
        assertEquals(expected, actual)
    }

    @Test
    fun testCardReaderFromString() {
        val expected = DeviceType.CARD
        val actual = DeviceType.fromString("crd")
        assertEquals(expected, actual)
    }

    @Test
    fun testColourSensorFromString() {
        val expected = DeviceType.COLOUR_SENSOR
        val actual = DeviceType.fromString("tcs")
        assertEquals(expected, actual)
    }

    @Test
    fun testThrowsUnknownDeviceException() {
        assertThrows<UnknownDeviceTypeException> {
            DeviceType.fromString("aaa")
        }
    }
}