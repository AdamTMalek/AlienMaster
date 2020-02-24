package app.serialcom

import com.fazecast.jSerialComm.SerialPort

/**
 * The [Serial] interface defines method that the implementation
 * of a serial communication interface should implement.
 */
interface Serial {
    /**
     * Returns all ports available to use
     */
    fun getAllAvailablePorts(): Collection<SerialPort>

    /**
     * Returns the port to which the interface is currently connected
     */
    fun getActivePort(): SerialPort?

    /**
     * Connects to the given port
     */
    fun connectTo(port: SerialPort)

    /**
     * Returns true if the serial is connected to some port
     */
    fun isConnected(): Boolean

    /**
     * Adds listener that receives all incoming data
     */
    fun addDataReceivedListener(listener: OnSerialDataReceivedListener)

    fun removeDataReceivedListener(listener: OnSerialDataReceivedListener)

    /**
     * Adds listener that receives notification when the collection
     * of available ports has changed
     */
    fun addPortListener(listener: OnAvailablePortsChangeListener)

    fun removePortListener(listener: OnAvailablePortsChangeListener)

    /**
     * Sends data to the currently connected port
     */
    fun send(data: String)
}