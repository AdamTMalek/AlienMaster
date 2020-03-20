package view

import app.IPlayer
import app.PlayerGenerator
import app.PlayersDatabase
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.concurrent.Task
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Cursor
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
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
    private var playersTable = TableView<IPlayer>()

    @FXML
    private var idColumn = TableColumn<IPlayer, Int>()

    @FXML
    private var nameColumn = TableColumn<IPlayer, String>()

    @FXML
    private var langColumn = TableColumn<IPlayer, String>()

    @FXML
    private var scoreColumn = TableColumn<IPlayer, Int>()

    private val scene by lazy { playersTable.scene }

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
        setupTable()
        loadPlayers()
    }

    /**
     * Sets up the players table by setting up the cell value factories and cell factories.
     */
    private fun setupTable() {
        idColumn.cellValueFactory = PropertyValueFactory("id")
        nameColumn.cellValueFactory = PropertyValueFactory("name")
        nameColumn.cellFactory = TextFieldTableCell.forTableColumn()
        nameColumn.setOnEditCommit {
            PlayersDatabase.changeName(it.rowValue, it.newValue)
            loadPlayers()
        }

        langColumn.cellValueFactory = PropertyValueFactory("language")
        langColumn.cellFactory = TextFieldTableCell.forTableColumn()
        langColumn.setOnEditCommit {
            PlayersDatabase.changeLanguage(it.rowValue, it.newValue)
            loadPlayers()
        }

        scoreColumn.cellValueFactory = PropertyValueFactory("score")
        scoreColumn.cellFactory = TextFieldTableCell.forTableColumn(IntegerStringConverter())
        scoreColumn.setOnEditCommit {
            PlayersDatabase.changeScore(it.rowValue, it.newValue)
            loadPlayers()
        }
    }

    fun close() {
        playersTable.scene.window.hide()
    }

    /**
     * Shows the generator dialog and (unless cancel was clicked) adds generated players
     * to the database.
     */
    fun generatePlayers() {
        val randomiseScore = when (showGeneratorDialogAndWait()) {
            ButtonType.YES -> true
            ButtonType.NO -> false
            else -> return
        }

        scene.cursor = Cursor.WAIT

        // The following task will be executed in a separate thread
        val task = object : Task<Unit>() {
            override fun call() {
                val generator = PlayerGenerator()
                val players = generator.generatePlayers(16, randomiseScore)

                addPlayersToTheDatabase(players)
            }
        }

        // Executed when players were added to the database
        task.setOnSucceeded {
            loadPlayers()
            scene.cursor = Cursor.DEFAULT
        }

        // Start the task
        Thread(task).start()
    }

    /**
     * Show the generator dialog allowing users to choose if the generated players
     * should have randomised scores or not.
     * @return Type of the button that was clicked
     */
    private fun showGeneratorDialogAndWait(): ButtonType {
        val alert = Alert(
            Alert.AlertType.NONE,
            "Randomise scores?",
            ButtonType.YES, ButtonType.NO, ButtonType.CANCEL
        ).apply {
            title = "Player Generator"
        }

        return alert.showAndWait().get()
    }

    /**
     * Adds each player from the list of [players] to the database.
     */
    private fun addPlayersToTheDatabase(players: List<PlayerGenerator.Player>) {
        players.forEach { player ->
            PlayersDatabase.addPlayer(player.name, player.language.code, player.score)
        }
    }

    /**
     * Called when Remove All Players button gets clicked
     */
    fun removeAllPlayers() {
        PlayersDatabase.dropPlayersTable()
        PlayersDatabase.createPlayersTable()

        loadPlayers()
    }

    /**
     * Called when Add Player button gets clicked
     */
    fun addPlayer() {
        AddPlayerController.showView()
        loadPlayers()
    }

    override fun changed(observable: ObservableValue<out String>?, oldValue: String?, newValue: String?) {
        loadPlayers()
    }

    /**
     * Loads players from the database and adds them to the [playersTable]
     */
    private fun loadPlayers() {
        val players = PlayersDatabase.getAllPlayers()
        playersTable.items = FXCollections.observableList(players)
    }
}