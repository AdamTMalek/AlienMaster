package app.serialcom

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ActionFromYamlBuilderTest {
    @Test
    fun testNothingSet() {
        val builder = ActionFromYamlBuilder()
        assertFalse(builder.isReady())
    }

    @Test
    fun testActionNotSet() {
        val builder = ActionFromYamlBuilder().set("value: 5")
        assertFalse(builder.isReady())
    }

    @Test
    fun testValueNotSet() {
        val builder = ActionFromYamlBuilder().set("action: setX")
        assertFalse(builder.isReady())
    }

    @Test
    fun testThrowsExceptionWhenNotReady() {
        val builder = ActionFromYamlBuilder()
        assertThrows<IllegalStateException> { builder.build() }
    }

    @Test
    fun testSetFields() {
        val action = ActionFromYamlBuilder()
            .set("action: setX")
            .set("value: 521")
            .build()

        val expected = Action("setX", "521")

        assertEquals(expected, action)
    }

    @Test
    fun testValueCharacters() {
        val action = ActionFromYamlBuilder()
            .set("action: setX")
            .set("value: .:Aa00")
            .build()

        val expected = Action("setX", ".:Aa00")

        assertEquals(expected, action)
    }
}