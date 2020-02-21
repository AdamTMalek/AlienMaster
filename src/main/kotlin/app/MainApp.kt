package app

import app.serialcom.OnAvailablePortsChangeListener
import app.serialcom.OnSerialDataReceivedListener
import app.serialcom.SerialCommunication
import com.fazecast.jSerialComm.SerialPort
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import view.EditorViewController
import view.MainViewController
import view.MainViewRequestListener
import view.SerialCommController

class MainApp : Application(), OnSerialDataReceivedListener, SerialCommController, MainViewRequestListener,
    OnAvailablePortsChangeListener {
    private lateinit var controller: MainViewController
    private val serialCommunication = SerialCommunication()

    override fun start(primaryStage: Stage?) {
        val url = this::class.java.classLoader.getResource("view/main_view.fxml")
        val loader = FXMLLoader().apply { location = url }
        val root = loader.load<Parent>()
        val scene = Scene(root, 700.0, 650.0)

        primaryStage?.let {
            it.title = "AlienMaster"
            it.scene = scene
            it.show()
        }

        controller = loader.getController<MainViewController>()
        controller.ports.addAll(serialCommunication.getAllPorts())
        serialCommunication.addDataReceivedListener(this)
        serialCommunication.addPortsReceivedListener(this)
        controller.setHandler(this)
        controller.setRequestListener(this)
        controller.setStage()
    }

    override fun onAvailablePortsChange() {
        Platform.runLater {
            val availablePorts = serialCommunication.getAllPorts()
            controller.ports.clear()
            controller.ports.addAll(availablePorts)
        }
    }

    override fun getPorts(): List<SerialPort> {
        return serialCommunication.getAllPorts()
    }

    override fun connectToPort(port: SerialPort) {
        serialCommunication.connectToPort(port)
    }

    override fun onDataReceived(data: String) {
        controller.onDataReceived(data)
    }

    override fun sendData(data: String) {
        serialCommunication.sendData(data)
    }

    override fun stop() {
        serialCommunication.stop()
    }

    override fun onOpenPlayerEditor() {
        EditorViewController.showView()
    }
}