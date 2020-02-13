package view

import javafx.animation.Interpolator
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.scene.Parent
import javafx.scene.layout.Pane
import javafx.util.Duration

object LoadAnimation {

    /**
     * Performs the animated transition sliding in the given [Parent] from the right to the left.
     * @param root Root pane of the original view (currently displayed, before this view will appear)
     * @param parent Parent of the end_screen FXML
     */
    fun loadWithAnimation(root: Pane, parent: Parent) {
        val scene = root.scene

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