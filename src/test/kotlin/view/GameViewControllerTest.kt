package view

import app.DatabaseMock
import app.serialcom.*
import com.fazecast.jSerialComm.SerialPort
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage
import org.junit.jupiter.api.Assertions.assertFalse
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
        database.addPlayer("Sam", "eng", 0)
        database.addPlayer("Jack", "ger", 0)
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

    @Test
    fun testPlayerDetected() {
        // Send Player Detected message
        val message = StateMessage(State.PLAYER_DETECTED, null).toYaml()
        serial.dataListeners.first().onDataReceived(message)

        Thread.sleep(3000) // Wait for the view to load

        // Check if the root contains the splash screen
        val splashScreen = lookup("#splashScreenRoot").tryQuery<Node>()
        assertTrue(splashScreen.isPresent)
    }

    @Test
    fun testCardInserted() {
        // Send Card Inserted message
        val message = StateMessage(State.CARD_INSERTED, 1).toYaml()
        serial.dataListeners.first().onDataReceived(message)

        // Wait for the view to load. Due to the TTS we have to wait for a good while
        Thread.sleep(7000)

        // Check if the root contains the welcome screen
        val welcomeScreen = lookup("#welcomeScreenRoot").tryQuery<Node>()
        assertTrue(welcomeScreen.isPresent)
    }

    @Test
    fun testPlayingGame() {
        // In this case we expect whatever is currently displayed (welcome screen)
        // to be deleted from the root
        // So, we test the welcome screen and then send another message and check
        // if it was removed

        // Load welcome screen
        val cardInsertedMessage = StateMessage(State.CARD_INSERTED, 1).toYaml()
        serial.dataListeners.first().onDataReceived(cardInsertedMessage)
        Thread.sleep(7_000)

        // Send playing message
        val playingMessage = StateMessage(State.PLAYING, null).toYaml()
        serial.dataListeners.first().onDataReceived(playingMessage)

        Thread.sleep(500)

        val welcomeScreen = lookup("#welcomeScreenRoot").tryQuery<Node>()
        assertFalse(welcomeScreen.isPresent)
    }

    @Test
    fun testGameOver() {
        // Load player
        val cardInsertedMessage = StateMessage(State.CARD_INSERTED, 1).toYaml()
        serial.dataListeners.first().onDataReceived(cardInsertedMessage)
        Thread.sleep(7000)

        val message = StateMessage(State.GAME_OVER, 21).toYaml()
        serial.dataListeners.first().onDataReceived(message)

        Thread.sleep(7000)

        val gameOverScreen = lookup("#endScreenRoot").tryQuery<Node>()
        assertTrue(gameOverScreen.isPresent)
    }
}