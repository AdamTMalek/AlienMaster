package view

import app.Player
import app.PlayersDatabase
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.stage.Stage
import javafx.util.converter.IntegerStringConverter
import java.net.URL
import java.util.*

class EditorViewController : Initializable, ChangeListener<String> {
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
        nameColumn.cellFactory = TextFieldTableCell.forTableColumn()
        nameColumn.setOnEditCommit {
            PlayersDatabase.changeName(it.rowValue, it.newValue)
            loadPlayers()
        }

        langColumn.cellValueFactory = PropertyValueFactory<Player, String>("language")
        langColumn.cellFactory = TextFieldTableCell.forTableColumn()
        langColumn.setOnEditCommit {
            PlayersDatabase.changeLanguage(it.rowValue, it.newValue)
            loadPlayers()
        }

        scoreColumn.cellValueFactory = PropertyValueFactory<Player, Int>("score")
        scoreColumn.cellFactory = TextFieldTableCell.forTableColumn(IntegerStringConverter())
        scoreColumn.setOnEditCommit {
            PlayersDatabase.changeScore(it.rowValue, it.newValue)
            loadPlayers()
        }

        loadPlayers()
    }


    fun close() {
    }

    fun removeAllPlayers() {
        PlayersDatabase.dropPlayersTable()
        PlayersDatabase.createPlayersTable()

        loadPlayers()
    }

    fun addPlayer() {
        AddPlayerController.showView()
        loadPlayers()
    }

    override fun changed(observable: ObservableValue<out String>?, oldValue: String?, newValue: String?) {
        loadPlayers()
    }

    private fun loadPlayers() {
        val players = PlayersDatabase.getAllPlayers()
        playersTable.items = FXCollections.observableList(players)
    }
}