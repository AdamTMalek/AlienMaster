package view

import app.Language
import app.Player
import app.PlayersDatabase
import app.serialcom.OnSerialDataReceivedListener
import com.fazecast.jSerialComm.SerialPort
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.layout.Pane
import javafx.stage.Stage
import java.net.URL
import java.util.*

class MainViewController : Initializable, OnSerialDataReceivedListener, ChangeListener<SerialPort> {
    @FXML
    private var rootPane = Pane()
    @FXML
    private var portChoice = ChoiceBox<SerialPort>()
    @FXML
    private var userInput = TextField()
    @FXML
    private var dataReceived = TextArea()

    private lateinit var handler: SerialCommController
    private var listener: MainViewRequestListener? = null
    val ports = FXCollections.observableArrayList<SerialPort>()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        portChoice.items = ports
        portChoice.selectionModel.selectedItemProperty().addListener(this)
    }

    /**
     * TODO: Delete after testing
     */
    fun setStage() {
        rootPane.scene.setOnKeyPressed { key ->
            val player = when (key.code) {
                KeyCode.G -> getGermanPlayer()
                KeyCode.E -> getEnglishPlayer()
                KeyCode.L -> getLastPlayer()
                KeyCode.C -> {
                    SplashScreenController.loadWithAnimation(rootPane); return@setOnKeyPressed
                }
                KeyCode.W -> {
                    val player = getGermanPlayer()
                    WelcomeScreenController.loadWithAnimation(rootPane, player); return@setOnKeyPressed
                }
                else -> return@setOnKeyPressed
            }

            val isNewTopScore = key.isShiftDown
            loadEndScreen(player, isNewTopScore)
        }
    }

    /**
     * TODO: Delete after testing
     */
    private fun getGermanPlayer(): Player {
        return PlayersDatabase.getAllPlayers().find { it.language == Language.GER.code }!!
    }

    /**
     * TODO: Delete after testing
     */
    private fun getEnglishPlayer(): Player {
        return PlayersDatabase.getAllPlayers().find { it.language == Language.ENG.code }!!
    }

    /**
     * TODO: Delete after testing
     */
    private fun getLastPlayer(): Player {
        return PlayersDatabase.getAllPlayers().minBy { it.score }!!
    }

    private fun loadEndScreen(player: Player, isNewTopScore: Boolean) {
        EndScreenController.loadWithAnimation(rootPane, player, 1, isNewTopScore)
    }

    fun setRequestListener(listener: MainViewRequestListener) {
        this.listener = listener
    }

    fun sendData() {
        val data = userInput.text
        dataReceived.text += "> $data\n"
        handler.sendData(data)
    }

    fun setHandler(handler: SerialCommController) {
        this.handler = handler
    }

    override fun onDataReceived(data: String) {
        dataReceived.text += "< $data\n"
    }

    override fun changed(observable: ObservableValue<out SerialPort>?, oldValue: SerialPort?, newValue: SerialPort?) {
        val port = newValue ?: return
        handler.connectToPort(port)
    }

    fun openPlayersEditor() {
        listener?.onOpenPlayerEditor()
    }

    fun close() {
        val stage = rootPane.scene.window as Stage
        stage.close()
    }
}