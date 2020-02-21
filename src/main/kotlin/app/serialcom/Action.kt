package app.serialcom

/**
 * The [Action] class is used for exchanging data
 * between the micro-controller and the PC
 * via serial communication.
 *
 * [Action] objects will be constructed by parsing
 * the incoming string data using the [ActionFromYamlBuilder]
 *
 * [value] is an optional field. However, as long as
 * the [ActionFromYamlBuilder] was used to create the object
 * the [value] will not be null if the [action] is of type
 * [ActionType.SET] or [ActionType.REPORT]
 */
data class Action(val action: ActionType, val deviceType: DeviceType, val deviceId: Int, val value: Int?)
