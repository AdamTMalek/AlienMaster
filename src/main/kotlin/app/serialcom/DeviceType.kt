package app.serialcom

/**
 * The [DeviceType] class indicates the type of the device
 * that is used in the data communication between the
 * micro-controller and the PC.
 * It is part of the [Action] class.
 */
enum class DeviceType(val code: String) {
    BUTTON("BTN"),
    LED("LED"),
    SERVO("SRV"),
    DISTANCE_SENSOR("DST"),
    CARD("CRD"),
    COLOUR_SENSOR("TCS");

    companion object {
        /**
         * Parse the given [typeCode] (three letter code taken from the
         * device line, without the id) into the equivalent enum value.
         * @throws UnknownDeviceTypeException when no equivalent value was found
         */
        @Throws(UnknownDeviceTypeException::class)
        fun fromString(typeCode: String): DeviceType {
            return values().find { it.code == typeCode.toUpperCase() }
                ?: throw UnknownDeviceTypeException(typeCode)
        }
    }
}

/**
 * Raised when no enum value was found when parsing it from string (using [DeviceType.fromString])
 */
class UnknownDeviceTypeException(type: String) : Exception("Unknown device type $type")