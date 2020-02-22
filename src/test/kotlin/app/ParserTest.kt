package app

import app.serialcom.Action
import app.serialcom.StateMessage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class ParserTest {
    /**
     * Test if the Parser behaves correctly when passing
     * a complete message to the parse function
     */
    @Test
    fun testParserWithCompleteMessage() {
        val message = """
            action: SET
            device: LED0
            value: 1
        """.trimIndent()

        var actionReceived = false
        val listener = object : OnMessageReceivedListener {
            override fun onActionReceived(action: Action) {
                actionReceived = true
            }

            override fun onStateReceived(state: StateMessage) {
                fail("This test expected onActionReceived to be invoked, not onStateReceived.")
            }
        }

        val parser = MessageParser()
        parser.addListener(listener)
        parser.parse(message)

        assertTrue(actionReceived)
    }

    /**
     * Test if the Parser behaves correctly when passing
     * a message line by line to the parse function
     */
    @Test
    fun testParserLineByLine() {
        val message = """
            action: SET
            device: LED0
            value: 1
        """.trimIndent()

        var actionReceived = false
        val listener = object : OnMessageReceivedListener {
            override fun onActionReceived(action: Action) {
                actionReceived = true
            }

            override fun onStateReceived(state: StateMessage) {
                fail("This test expected onActionReceived to be invoked, not onStateReceived.")
            }
        }

        val parser = MessageParser()
        parser.addListener(listener)

        message.lines().forEach { line ->
            parser.parse(line)
        }

        assertTrue(actionReceived)
    }

    @Test
    fun testParsesAction() {
        val message = """
            action: SET
            device: LED0
            value: 1
        """.trimIndent()

        var receivedAction: Action? = null
        val listener = object : OnMessageReceivedListener {
            override fun onActionReceived(action: Action) {
                receivedAction = action
            }

            override fun onStateReceived(state: StateMessage) {
            }
        }

        val parser = MessageParser()
        parser.addListener(listener)
        parser.parse(message)

        assertNotNull(receivedAction)
    }

    @Test
    fun testParsesState() {
        val message = """
            state: OVER
            value: 20
        """.trimIndent()

        var receivedState: StateMessage? = null
        val listener = object : OnMessageReceivedListener {
            override fun onActionReceived(action: Action) {
            }

            override fun onStateReceived(state: StateMessage) {
                receivedState = state
            }
        }

        val parser = MessageParser()
        parser.addListener(listener)
        parser.parse(message)

        assertNotNull(receivedState)
    }

    @Test
    fun testRemoveListener() {
        val message = """
            state: OVER
            value: 20
        """.trimIndent()

        var receivedState: StateMessage? = null
        val listener = object : OnMessageReceivedListener {
            override fun onActionReceived(action: Action) {
            }

            override fun onStateReceived(state: StateMessage) {
                receivedState = state
            }
        }

        MessageParser().apply {
            // add and remove the listener, if we still get the message
            // then the remove method does not work
            addListener(listener)
            remove(listener)

            parse(message)
        }

        assertNull(receivedState)
    }
}