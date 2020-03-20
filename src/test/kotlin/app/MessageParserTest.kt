package app

import app.serialcom.Action
import app.serialcom.StateMessage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class MessageParserTest {
    /**
     * Test if the Parser behaves correctly when passing
     * a complete message to the parse function
     */
    @Test
    fun testParserWithCompleteMessage() {
        val message = "{\n\taction: set\n\tdevice: LED0\n\tvalue: 1\n}"

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
        val message = "{\n\taction: set\n\tdevice: LED0\n\tvalue: 1\n}"

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
        val message = "{\n\taction: set\n\tdevice: LED0\n\tvalue: 1\n}"

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
    fun testParsesActionWithMultipleValues() {
        val message = "{\n\taction: report\n\tdevice: TCS0\n\tvalue: 1, 2, 3, 4\n}"

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

        assertEquals(listOf(1, 2, 3, 4), receivedAction!!.values)
    }

    @Test
    fun testParsesState() {
        val message = "{\n\tstate: OVER\n\tvalue: 20\n}"

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
        val message = "{\n\tstate: OVER\n\tvalue: 20\n}"

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