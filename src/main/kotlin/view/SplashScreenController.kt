package view

import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.layout.Pane
import java.net.URL
import java.util.*

class SplashScreenController : Initializable {
    companion object {
        fun loadWithAnimation(root: Pane) {
            val loader = loadFxml()
            val parent = loader.load<Parent>()

            LoadAnimation.loadWithAnimation(root, parent)
        }

        private fun loadFxml(): FXMLLoader {
            val resource = this::class.java.classLoader.getResource("view/splash_screen.fxml")
            return FXMLLoader().apply { location = resource }
        }
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
    }
}