package view

import app.*
import app.serialcom.*
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage
import java.net.URL
import java.util.*

class WaitingViewController : Initializable, OnSerialDataReceivedListener, OnMessageReceivedListener {
    @FXML
    private var emptyViewRoot = Pane()

    private lateinit var serial: Serial

    private lateinit var player: Player

    // The message parser will be used for creating messages out of incoming yaml from serial
    private val messageParser = MessageParser()

    companion object {
        fun showAndWait(serial: Serial) {
            val url = this::class.java.classLoader.getResource("view/empty_view.fxml")
            val loader = FXMLLoader().apply { location = url }
            val root = loader.load<Parent>()
            val scene = Scene(root, 500.0, 500.0)

            val controller = loader.getController<WaitingViewController>()
            controller.setSerial(serial)

            Stage().apply {
                title = "AlienMaster"
                this.scene = scene
                this.isFullScreen = true
                this.showAndWait()
            }
        }
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        messageParser.addListener(this)
    }

    private fun setSerial(serial: Serial) {
        this.serial = serial
    }

    override fun onActionReceived(action: Action) {
        // Ignore
    }

    override fun onStateReceived(state: StateMessage) {
        removeAllChildren()

        when (state.state) {
            State.WAITING -> return
            State.PLAYER_DETECTED -> SplashScreenController.loadWithAnimation(emptyViewRoot)
            State.CARD_INSERTED -> onCardInserted(state)
            State.PLAYING -> return
            State.GAME_OVER -> onGameOver(state)
        }
    }

    private fun removeAllChildren() {
        emptyViewRoot.children.removeAll()
    }

    private fun onCardInserted(state: StateMessage) {
        val playerId = state.value!!
        player = PlayersDatabase.getPlayerById(playerId)
            ?: throw PlayerNotFoundException(playerId)

        WelcomeScreenController.loadWithAnimation(emptyViewRoot, player)
    }

    private fun onGameOver(state: StateMessage) {
        val score = state.value!!
        val managedToBeatTopScore = score > player.score

        EndScreenController.loadWithAnimation(emptyViewRoot, player, score, managedToBeatTopScore)
    }

    override fun onDataReceived(data: String) {
        messageParser.parse(data)
    }
}
