package view

import app.serialcom.Serial
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import java.net.URL
import java.util.*

class WaitingViewController : Initializable {
    private lateinit var serial: Serial

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
    }

    private fun setSerial(serial: Serial) {
        this.serial = serial
    }
}
