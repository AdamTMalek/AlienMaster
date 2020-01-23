package view

import app.Player
import app.PlayersDatabase
import app.tts.TtsLanguage
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import javafx.stage.Stage
import java.net.URL
import java.util.*

class AddPlayerController : Initializable {
    @FXML
    private var rootPane = Pane()
    @FXML
    private var nameInput = TextField()
    @FXML
    private var langChoice = ChoiceBox<TtsLanguage>()
    @FXML
    private var scoreInput = TextField()

    companion object {
        private var player: Player? = null

        fun showView(): Player? {
            val url = this::class.java.classLoader.getResource("view/add_player_view.fxml")
            val loader = FXMLLoader().apply { location = url }
            val root = loader.load<Parent>()
            val scene = Scene(root, 375.0, 250.0)

            Stage().apply {
                title = "Add New Player"
                this.scene = scene
                this.showAndWait()
            }

            return player
        }
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        langChoice.items = FXCollections.observableList(TtsLanguage.values().toList())
    }

    fun cancel() {
        closeWindow()
    }

    fun save() {
        val name = nameInput.text
        val language = langChoice.selectionModel.selectedItem.code
        val score = scoreInput.text.toInt()

        PlayersDatabase.addPlayer(name, language, score)
        closeWindow()
    }

    private fun closeWindow() {
        rootPane.scene.window.hide()
    }
}