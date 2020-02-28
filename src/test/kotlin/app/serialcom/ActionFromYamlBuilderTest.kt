package app.serialcom

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ActionFromYamlBuilderTest {
    @Test
    fun testNothingSet() {
        assertFalse(ActionFromYamlBuilder().isReady())
    }

    @Test
    fun testThrowsExceptionWhenNotReady() {
        assertThrows<IllegalStateException> {
            ActionFromYamlBuilder().build()
        }
    }

    @Test
    fun testActionNotSet() {
        val incomingAction = "{\n\tdevice: CRD0\n\tvalue: 15\n}"

        val builder = ActionFromYamlBuilder()
        incomingAction.lines().forEach { line ->
            builder.set(line)
        }

        assertFalse(builder.isReady())
    }

    @Test
    fun testDeviceNotSet() {
        val incomingAction = "{\n\taction: get\n\tvalue: 15\n}"

        val builder = ActionFromYamlBuilder()
        incomingAction.lines().forEach { line ->
            builder.set(line)
        }

        assertFalse(builder.isReady())
    }

    @Test
    fun testValueNotSet() {
        val incomingAction = "{\n\taction: set\n\tdevice: SRV0\n}"

        val builder = ActionFromYamlBuilder()
        incomingAction.lines().forEach { line ->
            builder.set(line)
        }

        assertFalse(builder.isReady())
    }

    @Test
    fun testValueNotRequiredWhenActionIsGet() {
        val incomingAction = "{\n\taction: get\n\tdevice: LED0\n}"

        val builder = ActionFromYamlBuilder()
        incomingAction.lines().forEach { line ->
            builder.set(line)
        }

        assertTrue(builder.isReady())
    }

    @Test
    fun testParser() {
        val expected = Action(ActionType.REPORT, DeviceType.CARD, 0, 15)
        val incomingAction = "{\n\taction: report\n\tdevice: CRD0\n\tvalue: 15\n}"

        val builder = ActionFromYamlBuilder()
        incomingAction.lines().forEach { line ->
            builder.set(line)
        }

        val actual = builder.build()
        assertEquals(expected, actual)
    }
}