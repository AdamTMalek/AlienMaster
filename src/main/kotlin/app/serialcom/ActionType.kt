package app.serialcom

/**
 * The [ActionType] determines the type of action
 * the micro-controller or the PC wants the other part
 * to take.
 * It is a part of the [Action] class.
 */
enum class ActionType(val code: String) {
    GET("get"),
    SET("set"),
    REPORT("report");

    companion object {
        /**
         * Parses the value of action (i.e. the string after the colon)
         * to the equivalent enum value.
         * @throws UnknownDeviceTypeException when equivalent no value was found
         */
        @Throws(UnknownActionTypeException::class)
        fun fromString(code: String): ActionType {
            return values().find { it.code == code.toLowerCase() }
                ?: throw UnknownActionTypeException(code)
        }
    }
}

/**
 * Raised when no enum value was found when parsing it from string (using [ActionType.fromString])
 */
class UnknownActionTypeException(type: String) : Exception("Unknown action type $type")