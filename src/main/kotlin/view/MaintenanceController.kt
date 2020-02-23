package view

import app.OnMessageReceivedListener
import app.serialcom.Action
import app.serialcom.StateMessage
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import java.net.URL
import java.util.*

class MaintenanceController : Initializable, OnMessageReceivedListener {
    companion object {
        fun showAndWait() {
            val url = this::class.java.classLoader.getResource("view/maintenance_view.fxml")
            val loader = FXMLLoader().apply { location = url }
            val root = loader.load<Parent>()

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

    override fun onActionReceived(action: Action) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStateReceived(state: StateMessage) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}