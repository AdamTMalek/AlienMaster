package app.serialcom

/**
 * The [Action] class is used for exchanging data
 * between the micro-controller and the PC
 * via serial communication.
 *
 * [Action] objects will be constructed by parsing
 * the incoming string data using the [ActionFromYamlBuilder]
 */
data class Action(val action: ActionType, val deviceType: DeviceType, val deviceId: Int, val value: Int)
