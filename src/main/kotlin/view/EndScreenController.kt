package view

import javafx.animation.Interpolator
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.layout.Pane
import javafx.util.Duration
import java.net.URL
import java.util.*

class EndScreenController : Initializable {
    companion object {
        fun loadWithAnimation(root: Pane) {
            val resource = this::class.java.classLoader.getResource("view/end_screen.fxml")
            val loader = FXMLLoader().apply { location = resource }
            val parent = loader.load<Parent>()
            val scene = root.scene

            // Slide in from the right
            parent.translateXProperty().set(scene.width)
            root.children.add(parent)

            val keyValue = KeyValue(parent.translateXProperty(), 0, Interpolator.EASE_IN)
            val keyFrame = KeyFrame(Duration.seconds(1.0), keyValue)

            Timeline().apply {
                keyFrames.add(keyFrame)
                play()
            }
        }
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
    }
}