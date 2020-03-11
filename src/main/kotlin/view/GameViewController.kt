package view

import app.*
import app.serialcom.*
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.layout.Pane
import javafx.stage.Stage
import java.net.URL
import java.util.*

/**
 * The [GameViewController] is the main controller for the game window.
 * Because different views get attached to the root of this view, the
 * controller always exists as long as the game window is opened.
 *
 * The controller will transition with animation to the next "state"
 * view (e.g. splash screen, welcome screen or end screen) based on
 * the state message received from the serial port.
 */
class GameViewController : Initializable, OnSerialDataReceivedListener, OnMessageReceivedListener {
    @FXML
    private var emptyViewRoot = Pane()

    private lateinit var serial: Serial
    private lateinit var playersDatabase: PlayersDatabaseStorage

    private lateinit var player: Player

    // The message parser will be used for creating messages out of incoming yaml from serial
    private val messageParser = MessageParser()

    companion object {
        fun showAndWait(serial: Serial, database: PlayersDatabaseStorage) {
            val url = this::class.java.classLoader.getResource("view/empty_view.fxml")
            val loader = FXMLLoader().apply { location = url }
            val root = loader.load<Parent>()
            val scene = Scene(root, 500.0, 500.0)

            val controller = loader.getController<GameViewController>()
            controller.playersDatabase = database
            controller.setSerial(serial)

            Stage().apply {
                title = "AlienMaster"
                this.scene = scene
                this.isFullScreen = true
                this.setOnHidden {
                    serial.removeDataReceivedListener(controller)
                    controller.messageParser.remove(controller)
                }
                this.showAndWait()
            }
        }
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        messageParser.addListener(this)

        Platform.runLater {
            addDebugKeyPressHandler()
        }
    }

    private fun setSerial(serial: Serial) {
        this.serial = serial
        serial.addDataReceivedListener(this)
        serial.send("{\n\tdebug: 0\n}")
    }

    override fun onActionReceived(action: Action) {
        // Ignore
    }

    override fun onStateReceived(state: StateMessage) {
        removeAllChildren()

        Platform.runLater {
            when (state.state) {
                State.WAITING -> return@runLater
                State.PLAYER_DETECTED -> SplashScreenController.loadWithAnimation(emptyViewRoot)
                State.CARD_INSERTED -> onCardInserted(state)
                State.PLAYING -> return@runLater
                State.GAME_OVER -> onGameOver(state)
            }
        }
    }

    private fun removeAllChildren() {
        emptyViewRoot.children.removeAll()
    }

    private fun onCardInserted(state: StateMessage) {
        val playerId = state.value!!
        player = playersDatabase.getPlayerById(playerId)
            ?: throw PlayerNotFoundException(playerId)

        WelcomeScreenController.loadWithAnimation(emptyViewRoot, player)
    }

    private fun onGameOver(state: StateMessage) {
        val newScore = state.value!!
        val managedToBeatTopScore = newScore > player.score
        if (managedToBeatTopScore)
            playersDatabase.changeScore(player, newScore)

        EndScreenController.loadWithAnimation(emptyViewRoot, player, newScore, managedToBeatTopScore)

    }

    override fun onDataReceived(data: String) {
        messageParser.parse(data)
    }

    /**
     * For debugging purposes, key presses can be used to trigger transitions.
     */
    private fun addDebugKeyPressHandler() {
        emptyViewRoot.scene.setOnKeyPressed { key ->
            removeAllChildren()
            when (key.code) {
                KeyCode.S -> debugDisplaySplashScreen()
                KeyCode.W -> debugDisplayWelcomeScreen(key.isShiftDown)
                KeyCode.E -> debugDisplayEndScreen(key.isShiftDown)
                else -> return@setOnKeyPressed
            }
        }
    }

    private fun getGermanPlayer(): Player {
        return playersDatabase.getAllPlayers().find { it.language == Language.GER.code }!!
    }

    private fun getEnglishPlayer(): Player {
        return playersDatabase.getAllPlayers().find { it.language == Language.ENG.code }!!
    }

    private fun getLastPlayer(): Player {
        return playersDatabase.getAllPlayers().minBy { it.score }!!
    }

    /**
     * Displays splash screen after key press (for debugging)
     */
    private fun debugDisplaySplashScreen() {
        SplashScreenController.loadWithAnimation(emptyViewRoot)
    }

    /**
     * Displays welcome screen after key press (for debugging)
     *
     * @param german Indicates if German player should be displayed, or English (when false).
     */
    private fun debugDisplayWelcomeScreen(german: Boolean) {
        val player = if (german) getGermanPlayer() else getEnglishPlayer()
        WelcomeScreenController.loadWithAnimation(emptyViewRoot, player)
    }

    /**
     * Displays end screen after key press (for debugging)
     *
     * @param german Indicates if German player should be displayed, or English (when false).
     */
    private fun debugDisplayEndScreen(german: Boolean) {
        val player = if (german) getGermanPlayer() else getLastPlayer()
        val (newScore, beaten) = if (german) {
            Pair(player.score + 1, true)
        } else {
            Pair(player.score - 1, false)
        }
        EndScreenController.loadWithAnimation(emptyViewRoot, player, newScore, beaten)
    }
}
