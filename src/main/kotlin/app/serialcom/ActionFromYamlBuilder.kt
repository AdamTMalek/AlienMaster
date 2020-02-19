package app.serialcom

/**
 * The [ActionFromYamlBuilder] takes a line coming from the serial port and builds an [Action] object by parsing
 * the line.
 * It exposes three public methods:
 * - [set] that automatically sets the right fields of the [Action] object
 * - [isReady] that indicates if the [Action] object is ready to be built or not
 * - [build] that builds [Action] and returns it
 */
class ActionFromYamlBuilder {
    private var actionType: ActionType? = null
    private var deviceType: DeviceType? = null
    private var deviceId: Int = -1
    private var value: Int = -1

    /**
     * Given the [line] set the correct field of the [Action] object
     */
    fun set(line: String): ActionFromYamlBuilder {
        val (key, value) = parseLineAsPair(line)

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
    fun isReady(): Boolean {
        return actionType != null
                && deviceType != null
                && deviceId != -1
                && value != -1
    }

    /**
     * Returns the [Action] object with the set fields
     * @throws IllegalStateException when some fields were not set
     */
    @Throws(IllegalStateException::class)
    fun build(): Action {
        if (!isReady())
            throw IllegalStateException("The object is not ready to be built")

        return Action(actionType!!, deviceType!!, deviceId, value)
    }

    private fun parseLineAsPair(line: String): Pair<String, String> {
        // First group is a match of a word or bunch of words to a colon,
        // the second is any alphanumerical character, dot or a colon.
        val regex = Regex("(\\w+): ([A-Za-z0-9:.]+)")
        val matchGroups = regex.find(line)!!.groups

        return Pair(matchGroups[1]!!.value, matchGroups[2]!!.value)
    }
}