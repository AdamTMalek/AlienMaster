package view

import app.Player
import app.PlayersDatabase
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.stage.Stage
import java.net.URL
import java.util.*

class EditorViewController : Initializable {
    @FXML
    private var playersTable = TableView<Player>()
    @FXML
    private var idColumn = TableColumn<Player, Int>()
    @FXML
    private var nameColumn = TableColumn<Player, String>()
    @FXML
    private var langColumn = TableColumn<Player, String>()
    @FXML
    private var scoreColumn = TableColumn<Player, Int>()

    companion object {
        fun showView() {
            val url = this::class.java.classLoader.getResource("view/editor_view.fxml")
            val loader = FXMLLoader().apply { location = url }
            val root = loader.load<Parent>()
            val scene = Scene(root, 650.0, 475.0)

            Stage().apply {
                title = "Player Editor"
                this.scene = scene
                this.showAndWait()
            }
        }
    }


    override fun initialize(location: URL?, resources: ResourceBundle?) {
        idColumn.cellValueFactory = PropertyValueFactory<Player, Int>("id")
        nameColumn.cellValueFactory = PropertyValueFactory<Player, String>("name")
        langColumn.cellValueFactory = PropertyValueFactory<Player, String>("language")
        scoreColumn.cellValueFactory = PropertyValueFactory<Player, Int>("score")

        loadPlayers()
    }


    fun close() {

    }

    fun addPlayer() {
        AddPlayerController.showView()
        loadPlayers()
    }

    private fun loadPlayers() {
        val players = PlayersDatabase.getAllPlayers()
        playersTable.items = FXCollections.observableList(players)
    }
}