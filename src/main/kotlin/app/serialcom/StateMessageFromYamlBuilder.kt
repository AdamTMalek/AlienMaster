package app.serialcom

/**
 * The [StateMessageFromYamlBuilder] takes a line coming from the serial port
 * and builds an [StateMessage] object by parsing the line.
 * It exposes three public methods:
 * - [set] that automatically sets the right fields of the [Action] object
 * - [isReady] that indicates if the [Action] object is ready to be built or not
 * - [build] that builds [Action] and returns it
 */
class StateMessageFromYamlBuilder : MessageFromYamlBuilder<StateMessage>() {
    private var state: State? = null
    private var value: Int? = null

    /**
     * Given the [line] set the correct field of the [StateMessage] object
     */
    override fun set(line: String): MessageFromYamlBuilder<StateMessage> {
        val (key, value) = parseLineAsPair(line)

        when (key) {
            "state" -> setState(value)
            "value" -> setValue(value)
        }

        return this
    }

    private fun setState(value: String) {
        this.state = State.fromString(value)
    }

    private fun setValue(value: String) {
        this.value = value.toInt()
    }

    /**
     * Returns true if the object is ready to be built
     */
    override fun isReady(): Boolean {
        if (state == null)
            return false

        return when (state) {
            State.CARD_INSERTED -> value != null
            State.GAME_OVER -> value != null
            else -> true
        }
    }

    /**
     * Returns the [StateMessage] object with the set fields
     * @throws IllegalStateException when some fields were not set
     */
    @Throws(IllegalStateException::class)
    override fun build(): StateMessage {
        if (!isReady())
            throw IllegalStateException("The object is not ready to be built")

        return StateMessage(state!!, value)
    }
}