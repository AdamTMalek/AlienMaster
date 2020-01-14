package app

import com.fazecast.jSerialComm.SerialPort
import kotlin.concurrent.thread


/**
 * The [SerialCommunication] provides methods for serial port communication using jSerialComm (a platform-independent
 * library). An open port is running on a separate thread. When data is read from the port, the class will invoke
 * [OnSerialDataReceivedListener.onDataReceived] on every registered listener (using [addListener]).
 */
class SerialCommunication {
    private val listeners = mutableListOf<OnSerialDataReceivedListener>()
    private var comPort: SerialPort? = null
    private var run = true
    private var running = false

    /**
     * Add [OnSerialDataReceivedListener] for it to be able to receive data from the serial port.
     */
    fun addListener(listener: OnSerialDataReceivedListener) {
        listeners.add(listener)
    }

    /**
     * Get all available ports
     */
    fun getAllPorts(): List<SerialPort> {
        return SerialPort.getCommPorts().toList()
    }

    /**
     * Connect to the given [port].
     */
    fun connectToPort(port: SerialPort) {
        if (running) {
            run = false
        }

        comPort = port
        comPort!!.openPort()
        run()
    }

    /**
     * Send the given [data] to the currently connected port.
     */
    fun sendData(data: String) {
        val dataBytes = data.toByteArray()
        comPort?.writeBytes(dataBytes, dataBytes.size.toLong())
    }

    /**
     * Send the received data to every registered listener
     */
    private fun notifyDataReceived(data: String) {
        listeners.forEach { it.onDataReceived(data) }
    }

    /**
     * Stop the polling thread and close the currently open port
     */
    fun stop() {
        run = false
        comPort?.closePort()
    }

    /**
     * This method will start a new thread that will keep polling the open com port for available data.
     */
    private fun run() {
        thread(start = true) {
            running = true
            while (run) {
                if (comPort!!.bytesAvailable() > 0) {
                    val buffer = ByteArray(comPort!!.bytesAvailable())
                    comPort!!.readBytes(buffer, buffer.size.toLong())
                    notifyDataReceived(buffer.toString(Charsets.UTF_8))
                }
                Thread.sleep(20)
            }
        }
    }
}