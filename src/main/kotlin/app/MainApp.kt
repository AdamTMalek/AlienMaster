package app

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

class MainApp : Application() {
    override fun start(primaryStage: Stage?) {
        val url = this::class.java.classLoader.getResource("view/main_view.fxml")
        val loader = FXMLLoader().apply { location = url }
        val root = loader.load<Parent>()
        val scene = Scene(root, 300.0, 300.0)

        primaryStage?.let {
            it.title = "AlienMaster"
            it.scene = scene
            it.show()
        }
    }
}