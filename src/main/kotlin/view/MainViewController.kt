package view

import app.serialcom.OnAvailablePortsChangeListener
import app.serialcom.Serial
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ChoiceBox
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

    fun openGameWindow() {
        GameViewController.showAndWait(serial)
    }

    fun close() {
        val stage = rootPane.scene.window as Stage
        serial.removePortListener(this)
        stage.close()
    }
}