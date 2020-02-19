package app.serialcom

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ActionTypeTest {

    @Test
    fun testGetFromString() {
        val expected = ActionType.GET
        val actual = ActionType.fromString("get")
        assertEquals(expected, actual)
    }

    @Test
    fun testReportFromString() {
        val expected = ActionType.REPORT
        val actual = ActionType.fromString("report")
        assertEquals(expected, actual)
    }

    @Test
    fun testThrowsUnknownActionTypeException() {
        assertThrows<UnknownActionTypeException> {
            ActionType.fromString("foo")
        }
    }
}