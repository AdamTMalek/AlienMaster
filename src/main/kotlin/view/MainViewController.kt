package view

import app.Language
import app.Player
import app.PlayersDatabase
import app.serialcom.OnAvailablePortsChangeListener
import app.serialcom.Serial
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ChoiceBox
import javafx.scene.input.KeyCode
import javafx.scene.layout.Pane
import javafx.stage.Stage
import java.net.URL
import java.util.*

class MainViewController : Initializable, OnAvailablePortsChangeListener {
    @FXML
    private var rootPane = Pane()

    @FXML
    private var portChoice = ChoiceBox<String>()

    private lateinit var serial: Serial

    override fun initialize(location: URL?, resources: ResourceBundle?) {
    }

    fun setSerial(serial: Serial) {
        this.serial = serial
        serial.addPortListener(this)

        Platform.runLater {
            setupPortChoice()
            addPortChoiceChangeListener()
        }
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

    override fun onAvailablePortsChange() {
        Platform.runLater {
            portChoice.items.clear()
            portChoice.items.addAll(getAllAvailablePortsNames())
        }
    }

    /**
     * Picks the current com port in the choice box if [Serial.getActivePort] returns non-null value
     */
    private fun setupPortChoice() {
        val serialNames = getAllAvailablePortsNames()

        portChoice.items.addAll(serialNames)

        val current = serial.getActivePort()
            ?: return

        val currentPortIndex = portChoice.items.indexOf(current.descriptivePortName)
        portChoice.selectionModel.select(currentPortIndex)
    }

    private fun addPortChoiceChangeListener() {
        portChoice.selectionModel.selectedItemProperty().addListener { _, _, newPort ->
            if (newPort == null)
                return@addListener

            val port = serial.getAllAvailablePorts().find { it.descriptivePortName == newPort }!!
            serial.connectTo(port)
        }
    }

    /**
     * Returns a list of descriptive port names of the available ports
     */
    private fun getAllAvailablePortsNames() = serial.getAllAvailablePorts().map { it.descriptivePortName }

    fun openMaintenanceWindow() {
        MaintenanceController.showAndWait(serial)
    }

    fun openPlayersEditor() {
        EditorViewController.showView()
    }

    fun close() {
        val stage = rootPane.scene.window as Stage
        serial.removePortListener(this)
        stage.close()
    }
}