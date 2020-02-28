package app

import app.serialcom.SerialCommunication
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import view.MainViewController

class MainApp : Application() {
    private lateinit var controller: MainViewController
    private val serial = SerialCommunication()

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
        controller.setSerial(serial)
        controller.setStage()
    }

    override fun stop() {
        serial.stop()
        super.stop()
    }
}