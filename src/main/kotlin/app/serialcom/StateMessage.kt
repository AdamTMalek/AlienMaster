package app.serialcom

/**
 * [StateMessage] class represents the state type of message
 * that the micro-controller (MBED) will send to the
 * PC to inform about state transitions.
 * It may also pass in the value depending on the
 * state.
 */
data class StateMessage(val state: State, val value: Int?) {
    /**
     * Translate the object into equivalent YAML representation
     */
    fun toYaml(): String {
        var yaml = "state: ${state.code}"
        if (value != null) {
            yaml += "\nvalue: $value"
        }

        return yaml
    }
}