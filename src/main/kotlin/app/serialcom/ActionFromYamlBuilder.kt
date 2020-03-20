package app.serialcom

/**
 * The [ActionFromYamlBuilder] takes a line coming from the serial port and builds an [Action] object by parsing
 * the line.
 * It exposes three public methods:
 * - [set] that automatically sets the right fields of the [Action] object
 * - [isReady] that indicates if the [Action] object is ready to be built or not
 * - [build] that builds [Action] and returns it
 */
class ActionFromYamlBuilder : MessageFromYamlBuilder<Action>() {
    private var actionType: ActionType? = null
    private var deviceType: DeviceType? = null
    private var deviceId: Int = -1
    private var value: Int? = null  // TODO: Change to list of ints

    /**
     * Given the [line] set the correct field of the [Action] object
     */
    override fun set(line: String): ActionFromYamlBuilder {
        val data = super.getMessageContent(line)

        if (data == "")
            return this

        val (key, value) = parseLineAsPair(data)

        when (key) {
            "action" -> actionType = ActionType.fromString(value)
            "device" -> setDeviceTypeAndId(value)
            "value" -> this.value = value.toInt()
        }

        return this
    }

    private fun setDeviceTypeAndId(string: String) {
        val type = string.take(3)
        val id = string.last()

        deviceType = DeviceType.fromString(type)
        deviceId = Character.getNumericValue(id)
    }

    /**
     * Returns true if the object is ready to be built
     */
    override fun isReady(): Boolean {
        return if (isValueRequired())
            hasRequiredFields() && value != null
        else
            hasRequiredFields()
    }

    /**
     * Checks if the required fields (i.e. action type and device information)
     * are present.
     */
    private fun hasRequiredFields() = actionType != null && deviceType != null && deviceId != -1

    /**
     * Returns true if the value is required for the action (depends on action type)
     */
    private fun isValueRequired(): Boolean {
        return if (actionType == null)
            true
        else
            actionType == ActionType.SET || actionType == ActionType.REPORT
    }

    /**
     * Returns the [Action] object with the set fields
     * @throws IllegalStateException when some fields were not set
     */
    @Throws(IllegalStateException::class)
    override fun build(): Action {
        if (!isReady())
            throw IllegalStateException("The object is not ready to be built")

        return Action(actionType!!, deviceType!!, deviceId, listOf(value!!))
    }
}