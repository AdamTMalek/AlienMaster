package app

import app.serialcom.Action
import app.serialcom.ActionFromYamlBuilder
import app.serialcom.StateMessage
import app.serialcom.StateMessageFromYamlBuilder

/**
 * The [MessageParser] class parses messages coming from the
 * virtual com port from the MBED into one of the two
 * possible messages, [Action] or [StateMessage].
 *
 * When an object of the two classes is built, the
 * parser will notify every registered listener
 * (registered using [addListener])
 * using one of its two methods.
 *
 * The string passed to the [parse] method can either be
 * a single line (incomplete message) or a complete
 * message.
 */
class MessageParser {
    /**
     * [State] determines the state of the parser
     */
    private enum class State {
        WAITING, // Default state
        PARSING_ACTION,
        PARSING_STATE
    }

    private val listeners = mutableListOf<OnMessageReceivedListener>()
    private var currentState = State.WAITING
    private var actionBuilder = ActionFromYamlBuilder()
    private var stateBuilder = StateMessageFromYamlBuilder()

    /**
     * Adds a [OnMessageReceivedListener] that will receive notifications
     * when the parser is finished parsing a message
     */
    fun addListener(listener: OnMessageReceivedListener) {
        listeners.add(listener)
    }

    /**
     * Removes the [OnMessageReceivedListener] from the list of listeners
     */
    fun remove(listener: OnMessageReceivedListener) {
        listeners.remove(listener)
    }

    /**
     * Parses complete or incomplete yaml message
     */
    fun parse(string: String) {
        val lines = string.lines()

        // Check what the first line contains to determine what builder will be used
        if (currentState == State.WAITING) {
            val isAction = lines.first().contains("action")

            currentState = if (isAction) State.PARSING_ACTION else State.PARSING_STATE
        }

        when (currentState) {
            State.PARSING_ACTION -> parseAction(lines)
            State.PARSING_STATE -> parseState(lines)
            else -> return
        }
    }

    /**
     * Parses lines as an [Action] using the [ActionFromYamlBuilder]
     */
    private fun parseAction(stringLines: Collection<String>) {
        stringLines.forEach { line ->
            actionBuilder.set(line)

            if (actionBuilder.isReady()) {
                val action = actionBuilder.build()
                actionBuilder = ActionFromYamlBuilder()
                currentState = State.WAITING

                notifyActionReceived(action)
            }
        }
    }

    /**
     * Parses lines as a [StateMessage] using the [StateMessageFromYamlBuilder]
     */
    private fun parseState(stringLines: Collection<String>) {
        stringLines.forEach { line ->
            stateBuilder.set(line)
        }

        if (stateBuilder.isReady()) {
            val state = stateBuilder.build()
            stateBuilder = StateMessageFromYamlBuilder()
            currentState = State.WAITING

            notifyStateReceived(state)
        }
    }

    /**
     * Notify every listener that [Action] has been received and pass it to the listeners
     */
    private fun notifyActionReceived(action: Action) {
        listeners.forEach { it.onActionReceived(action) }
    }

    /**
     * Notify every listener that [StateMessage] has been received and pass it to the listeners
     */
    private fun notifyStateReceived(state: StateMessage) {
        listeners.forEach { it.onStateReceived(state) }
    }
}