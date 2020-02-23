package app

import app.serialcom.Action
import app.serialcom.StateMessage

/**
 * Any class wanting to receive [Action] or [StateMessage]
 * from the micro-controller, using the [MessageParser]
 * must implement this interface.
 */
interface OnMessageReceivedListener {
    /**
     * Invoked when the parser has finished parsing an [Action]
     */
    fun onActionReceived(action: Action)

    /**
     * Invoked when the parser has finished parsing a [StateMessage]
     */
    fun onStateReceived(state: StateMessage)
}