package app.serialcom

/**
 * The [Action] class is used for exchanging data
 * between the micro-controller and the PC
 * via serial communication.
 *
 * [Action] objects will be constructed by parsing
 * the incoming string data using the [ActionFromYamlBuilder]
 *
 * [values] are optional. However, as long as
 * the [ActionFromYamlBuilder] was used to create the object
 * the [values] will not be an empty list if the [action] is of type
 * [ActionType.SET] or [ActionType.REPORT]
 */
data class Action(val action: ActionType, val deviceType: DeviceType, val deviceId: Int, val values: List<Int>) {
    /**
     * Translate the object into equivalent YAML representation
     */
    fun toYaml(): String {
        var yaml = "{\n\taction: ${action.code}\n\tdevice: ${deviceType.code}$deviceId"

        if (values.isNotEmpty()) {
            // The blank line is so that the value appears on the
            // next line. If it was not there, the value would
            // go right after the device id
            yaml += "\n\tvalue: ${values.joinToString()}"
        }

        return "$yaml\n}"
    }
}
