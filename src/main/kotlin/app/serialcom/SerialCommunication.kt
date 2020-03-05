package app.serialcom

import com.fazecast.jSerialComm.SerialPort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.thread


/**
 * The [SerialCommunication] provides methods for serial port communication using jSerialComm (a platform-independent
 * library). An open port is running on a separate thread. When data is read from the port, the class will invoke
 * [OnSerialDataReceivedListener.onDataReceived] on every registered listener (using [addDataReceivedListener]).
 */
class SerialCommunication : Serial {
    /**
     * The [SerialPortCheckActivity] is an asynchronous activity that is responsible
     * for checking if the list of available com ports has updated or not.
     */
    private class SerialPortCheckActivity : CoroutineScope by CoroutineScope(Dispatchers.Default) {

        /**
         * Starts the asynchronous checking coroutine.
         * @param ports Reference to a mutable collection of [SerialPort] objects
         * @param onUpdateCallback called when the collection has been modified.
         */
        fun checkForAvailablePorts(ports: MutableCollection<SerialPort>, onUpdateCallback: () -> Unit) {
            launch {
                while (true) {
                    val comPorts = SerialPort.getCommPorts().toList()

                    if (listsDifferent(comPorts, ports)) {
                        ports.clear()
                        ports.addAll(comPorts)

                        onUpdateCallback()
                    }

                    // Wait 100ms before next check
                    delay(100)
                }
            }
        }

        /**
         * Returns true if the two collections [a] and [b] are different.
         */
        private fun listsDifferent(a: Collection<SerialPort>, b: Collection<SerialPort>): Boolean {
            // Compare the two lists using port descriptions
            // SerialPort does not override equals or hashCode
            val same = a.all { it.portDescription in b.map { port -> port.portDescription } }

            return if (!same)
                true // some elements of a are not in b
            else
                a.size != b.size  // if size is the same - the lists are exactly the same
        }
    }

    private val dataReceivedListeners = mutableListOf<OnSerialDataReceivedListener>()
    private val portsChangedListeners = mutableListOf<OnAvailablePortsChangeListener>()
    private val availablePorts = mutableListOf<SerialPort>()
    private var comPort: SerialPort? = null
    private var runCommunicationThread = true
    private var communicationThreadRunning = false
    private val outgoingDataQueue: Queue<ByteArray> = LinkedList<ByteArray>()

    init {
        val portCheckActivity = SerialPortCheckActivity()
        portCheckActivity.checkForAvailablePorts(availablePorts, ::notifyPortsListChanged)
    }

    /**
     * Returns all ports available to use
     */
    override fun getAllAvailablePorts(): Collection<SerialPort> {
        return SerialPort.getCommPorts().toList()
    }

    /**
     * Returns the port to which the interface is currently connected
     */
    override fun getActivePort(): SerialPort? {
        return comPort
    }

    /**
     * Connects to the given [port].
     */
    override fun connectTo(port: SerialPort) {
        if (communicationThreadRunning) {
            runCommunicationThread = false
        }

        comPort = port
        comPort!!.openPort()
        run()
    }

    /**
     * Returns true if the serial is connected to some port
     */
    override fun isConnected() = communicationThreadRunning

    /**
     * Add [OnSerialDataReceivedListener] for it to be able to receive data from the serial port.
     */
    override fun addDataReceivedListener(listener: OnSerialDataReceivedListener) {
        dataReceivedListeners.add(listener)
    }

    override fun removeDataReceivedListener(listener: OnSerialDataReceivedListener) {
        dataReceivedListeners.remove(listener)
    }

    /**
     * Add [OnAvailablePortsChangeListener] for it to be able to get notified about port list updates
     */
    override fun addPortListener(listener: OnAvailablePortsChangeListener) {
        portsChangedListeners.add(listener)
    }

    override fun removePortListener(listener: OnAvailablePortsChangeListener) {
        portsChangedListeners.remove(listener)
    }

    /**
     * Send the given [data] to the currently connected port.
     * @throws IllegalStateException when the object is not connected to any port
     */
    @Throws(IllegalStateException::class)
    override fun send(data: String) {
        checkNotNull(comPort) { throw IllegalStateException("SerialCommunication is not connected to any port") }

        val dataBytes = data.toByteArray(Charsets.US_ASCII)
        outgoingDataQueue.add(dataBytes)
    }

    /**
     * Send the received data to every registered listener
     */
    private fun notifyDataReceived(data: String) {
        val string = data.lines().filterNot { it.isBlank() }.joinToString("\n")
        dataReceivedListeners.forEach { it.onDataReceived(string) }
    }

    /**
     * Send the notification about port list modification
     */
    private fun notifyPortsListChanged() {
        portsChangedListeners.forEach { it.onAvailablePortsChange() }
    }

    /**
     * Stop the polling thread and close the currently open port
     */
    fun stop() {
        runCommunicationThread = false
        comPort?.closePort()
    }

    /**
     * This method will start a new thread that will keep polling the open com port for available data.
     */
    private fun run() {
        thread(start = true) {
            communicationThreadRunning = true
            val stringBuffer = StringBuilder()

            while (runCommunicationThread) {
                if (comPort!!.bytesAvailable() > 0) {
                    readData(stringBuffer)

                    if (stringBuffer.last() == '\n') {
                        notifyDataReceived(stringBuffer.toString())
                        stringBuffer.clear()
                    }

                }

                sendDataFromQueue()
                Thread.sleep(500)
            }
        }
    }

    /**
     * Reads all available bytes from the communication port
     * into the [buffer].
     */
    private fun readData(buffer: StringBuilder) {
        val bytes = ByteArray(comPort!!.bytesAvailable())
        comPort!!.readBytes(bytes, bytes.size.toLong())

        buffer.append(bytes.toString(Charsets.US_ASCII))
    }

    /**
     * Checks if the [outgoingDataQueue] is not empty.
     * If it isn't, it will dequeue a byte array and
     * send it to the com port.
     */
    private fun sendDataFromQueue() {
        if (outgoingDataQueue.isEmpty())
            return

        val data = outgoingDataQueue.remove()
        comPort!!.writeBytes(data, data.size.toLong())
    }
}