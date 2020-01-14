package app

/**
 * This interface should be used by any classes that wants to receive data from a serial port must
 * implement this interface. Every time there is data available to read, the [SerialCommunication]
 * will read the available string and call this method.
 */
interface OnSerialDataReceivedListener {
    /**
     * Called after receiving data from the serial port.
     */
    fun onDataReceived(data: String)
}