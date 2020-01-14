package app

import com.fazecast.jSerialComm.SerialPort
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import view.MainViewController
import view.SerialCommController

class MainApp : Application(), OnSerialDataReceivedListener, SerialCommController {
    private lateinit var controller: MainViewController
    private val serialCommunication = SerialCommunication()

    override fun start(primaryStage: Stage?) {
        val url = this::class.java.classLoader.getResource("view/main_view.fxml")
        val loader = FXMLLoader().apply { location = url }
        val root = loader.load<Parent>()
        val scene = Scene(root, 600.0, 350.0)

        primaryStage?.let {
            it.title = "AlienMaster"
            it.scene = scene
            it.show()
        }

        controller = loader.getController<MainViewController>()
        controller.ports.addAll(serialCommunication.getAllPorts())
        serialCommunication.addListener(this)
        controller.setHandler(this)
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
}