package view

import com.fazecast.jSerialComm.SerialPort

/**
 * The [SerialCommController] defines methods that will be used by the [MainViewController] to
 * communicate with its master class to bind serial port view with some controller.
 */
interface SerialCommController {
    /**
     * Get the available serial ports
     */
    fun getPorts(): List<SerialPort>

    /**
     * Connect to the given serial port
     */
    fun connectToPort(port: SerialPort)

    /**
     * Send data to the currently connected serial port
     */
    fun sendData(data: String)
}