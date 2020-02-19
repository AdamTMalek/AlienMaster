package app.serialcom

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
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
        val incomingAction = """
            device: CRD0
            value: 15
        """.trimIndent()

        val builder = ActionFromYamlBuilder()
        incomingAction.lines().forEach { line ->
            builder.set(line)
        }

        assertFalse(builder.isReady())
    }

    @Test
    fun testDeviceNotSet() {
        val incomingAction = """
            action: get
            value: 15
        """.trimIndent()

        val builder = ActionFromYamlBuilder()
        incomingAction.lines().forEach { line ->
            builder.set(line)
        }

        assertFalse(builder.isReady())
    }

    @Test
    fun testValueNotSet() {
        val incomingAction = """
            action: get
            device: LED0
        """.trimIndent()

        val builder = ActionFromYamlBuilder()
        incomingAction.lines().forEach { line ->
            builder.set(line)
        }

        assertFalse(builder.isReady())
    }

    @Test
    fun testParser() {
        val expected = Action(ActionType.REPORT, DeviceType.CARD, 0, 15)
        val incomingAction = """
            action: report
            device: CRD0
            value: 15
        """.trimIndent()

        val builder = ActionFromYamlBuilder()
        incomingAction.lines().forEach { line ->
            builder.set(line)
        }

        val actual = builder.build()
        assertEquals(expected, actual)
    }
}