package view

import app.OnMessageReceivedListener
import app.serialcom.Action
import app.serialcom.OnAvailablePortsChangeListener
import app.serialcom.Serial
import app.serialcom.StateMessage
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.ChoiceBox
import javafx.stage.Stage
import java.net.URL
import java.util.*

class MaintenanceController : Initializable, OnMessageReceivedListener, OnAvailablePortsChangeListener {
    @FXML
    private var portChoice = ChoiceBox<String>()

    private lateinit var serial: Serial

    companion object {
        fun showAndWait(serial: Serial) {
            val url = this::class.java.classLoader.getResource("view/maintenance_view.fxml")
            val loader = FXMLLoader().apply { location = url }
            val root = loader.load<Parent>()

            val controller = loader.getController<MaintenanceController>()
            controller.setSerial(serial)

            val scene = Scene(root, 900.0, 700.0)
            Stage().apply {
                this.scene = scene
                this.title = "Maintenance Window"
                this.showAndWait()
            }
        }
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {

    }

    fun setSerial(serial: Serial) {
        this.serial = serial
        this.serial.addPortListener(this)
        setupPortChoice()
    }

    private fun setupPortChoice() {
        val serialNames = getAllAvailablePortsNames()

        portChoice.items.addAll(serialNames)

        val current = serial.getActivePort()
            ?: return

        val currentPortIndex = portChoice.items.indexOf(current.descriptivePortName)
        portChoice.selectionModel.select(currentPortIndex)
    }

    override fun onActionReceived(action: Action) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStateReceived(state: StateMessage) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAvailablePortsChange() {
        Platform.runLater {
            portChoice.items.clear()
            portChoice.items.addAll(getAllAvailablePortsNames())
        }
        println("Ports updated")
    }

    private fun getAllAvailablePortsNames() = serial.getAllAvailablePorts().map { it.descriptivePortName }
}