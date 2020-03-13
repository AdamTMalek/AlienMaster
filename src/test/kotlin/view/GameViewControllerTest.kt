package view

import app.DatabaseMock
import app.serialcom.OnAvailablePortsChangeListener
import app.serialcom.OnSerialDataReceivedListener
import app.serialcom.Serial
import com.fazecast.jSerialComm.SerialPort
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.ApplicationTest

@ExtendWith(ApplicationExtension::class)
class GameViewControllerTest : ApplicationTest() {
    private val serial = object : Serial {
        val dataListeners = mutableListOf<OnSerialDataReceivedListener>()
        val portListeners = mutableListOf<OnAvailablePortsChangeListener>()
        var lastMessage: String? = null

        override fun getAllAvailablePorts(): Collection<SerialPort> {
            return emptyList()
        }

        override fun getActivePort(): SerialPort? {
            return null
        }

        override fun connectTo(port: SerialPort) {

        }

        override fun isConnected(): Boolean {
            return true
        }

        override fun addDataReceivedListener(listener: OnSerialDataReceivedListener) {
            dataListeners.add(listener)
        }

        override fun removeDataReceivedListener(listener: OnSerialDataReceivedListener) {
            dataListeners.remove(listener)
        }

        override fun addPortListener(listener: OnAvailablePortsChangeListener) {
            portListeners.add(listener)
        }

        override fun removePortListener(listener: OnAvailablePortsChangeListener) {
            portListeners.remove(listener)
        }

        override fun send(data: String) {
            lastMessage = data
        }
    }

    private val database = DatabaseMock()

    init {
        database.addPlayer("Sam", "ENG", 0)
        database.addPlayer("Jack", "GER", 0)
    }

    override fun start(stage: Stage) {
        val root = GameViewController.loadRoot(serial, database)
        val scene = Scene(root, 900.0, 800.0)
        stage.scene = scene
        stage.show()
    }

    private fun getRoot() = lookup("#gameViewRoot").tryQuery<Pane>().get()

    @Test
    fun testRegistersSelfAsDataListener() {
        assertTrue(serial.dataListeners.isNotEmpty())
    }

    @Test
    fun testViewIsEmptyByDefault() {
        val root = getRoot()
        assertTrue(root.children.isEmpty())
    }
}