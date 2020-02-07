package view

import app.Player
import app.PlayersDatabase
import javafx.animation.*
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.Pane
import javafx.util.Duration
import java.net.URL
import java.util.*

class EndScreenController : Initializable {
    @FXML
    private var playersTable = TableView<Player>()
    @FXML
    private var positionColumn = TableColumn<Player, Int>()
    @FXML
    private var nameColumn = TableColumn<Player, String>()
    @FXML
    private var scoreColumn = TableColumn<Player, Int>()
    @FXML
    private var promptLabel = Label()

    private lateinit var player: Player
    private val allPlayers = PlayersDatabase.getAllPlayers().sortedByDescending { it.score }

    companion object {
        fun loadWithAnimation(root: Pane, player: Player) {
            val resource = this::class.java.classLoader.getResource("view/end_screen.fxml")
            val loader = FXMLLoader().apply { location = resource }
            val parent = loader.load<Parent>()
            val scene = root.scene

            with(loader.getController<EndScreenController>()) {
                setPlayer(player)
                populateTable()
            }

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

    fun setPlayer(player: Player) {
        this.player = player
    }

    fun populateTable() {
        addPlayersToTable()
        sortTable()
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        initColumns()
        addPulsingToPromptLabel()
    }

    private fun initColumns() {
        positionColumn.setCellFactory {
            object : TableCell<Player, Int>() {
                override fun updateIndex(i: Int) {
                    super.updateIndex(i)

                    text = if (isEmpty) {
                        ""
                    } else {
                        val row = this.tableRow ?: return
                        val playerAtRow = row.item

                        if (playerAtRow == player)
                            addHighlightStyleToRow(row)

                        val index = allPlayers.indexOf(playerAtRow)
                        val position = index + 1

                        position.toString()
                    }
                }
            }
        }
        nameColumn.cellValueFactory = PropertyValueFactory<Player, String>("name")
        scoreColumn.cellValueFactory = PropertyValueFactory<Player, Int>("score")
    }

    private fun addHighlightStyleToRow(row: TableRow<out Any?>) {
        row.styleClass.add("highlight")
    }

    private fun addPlayersToTable() {
        val players = mutableSetOf<Player>().apply {
            addAll(allPlayers.take(5))
            add(player)
        }

        playersTable.items = FXCollections.observableList(players.toList())
    }

    private fun sortTable() {
        scoreColumn.comparator = scoreColumn.comparator.reversed()
        playersTable.sortOrder.add(scoreColumn)
    }

    private fun addPulsingToPromptLabel() {
        FadeTransition(Duration.seconds(1.0), promptLabel).apply {
            fromValue = 1.0
            toValue = 0.0
            isAutoReverse = true
            cycleCount = Animation.INDEFINITE
            play()
        }
    }
}