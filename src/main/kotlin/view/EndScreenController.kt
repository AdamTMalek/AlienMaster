package view

import app.Language
import app.Player
import app.PlayersDatabase
import extensions.getParameterizedString
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
import view.EndScreenController.Companion.loadWithAnimation
import java.net.URL
import java.util.*

/**
 * The [EndScreenController] is the class responsible for controlling data
 * and interactions happening in the view defined by the end_screen.fxml.
 *
 * The controller defines a static method [loadWithAnimation] that loads the
 * view and makes an animated transition from the old view to this.
 */
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

    /**
     * The [textResourceBundle] contains all the locale-dependent translations of text
     * appearing in the view.
     */
    private val textResourceBundle: ResourceBundle by lazy {
        val language = Language.fromCode(currentPlayer.language)
        ResourceBundle.getBundle("TextBundle", language.locale)
    }

    private val allPlayers = PlayersDatabase.getAllPlayers().sortedByDescending { it.score }

    companion object {
        private lateinit var currentPlayer: Player // Player is going to be highlighted in the table
        private var score: Int = 0
        private var isNewTopScore: Boolean = false

        /**
         * Loads this view with an animated transition.
         */
        fun loadWithAnimation(root: Pane, player: Player, score: Int, isNewTopScore: Boolean) {
            currentPlayer = player
            this.score = score
            this.isNewTopScore = isNewTopScore

            val loader = loadFxml()
            val parent = loader.load<Parent>()

            animateTransition(root, parent)
        }

        private fun loadFxml(): FXMLLoader {
            val resource = this::class.java.classLoader.getResource("view/end_screen.fxml")
            return FXMLLoader().apply { location = resource }
        }

        /**
         * Performs the animated transition.
         * @param root Root pane of the original view (currently displayed, before this view will appear)
         * @param parent Parent of the end_screen FXML
         */
        private fun animateTransition(root: Pane, parent: Parent) {
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

    fun getThanksText(): String = textResourceBundle.getString("end_screen.thanks")

    fun getResultText(): String = textResourceBundle.getParameterizedString("end_screen.game_result", score)

    fun getTopScoreText(): String {
        return if (isNewTopScore)
            textResourceBundle.getString("end_screen.top_score.new")
        else
            textResourceBundle.getString("end_screen.top_score.old")
    }

    fun getTableDesc(): String = textResourceBundle.getString("end_screen.leader_board_desc")

    fun getCardPromptText(): String = textResourceBundle.getString("end_screen.card_reminder")

    fun getPromptText(): String = textResourceBundle.getString("end_screen.play_again_prompt")

    fun getPositionHeading(): String = textResourceBundle.getString("end_screen.table.position")

    fun getNicknameHeading(): String = textResourceBundle.getString("end_screen.table.nickname")

    fun getScoreHeading(): String = textResourceBundle.getString("end_screen.table.score")

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        initColumns()
        addPulsingToPromptLabel()
        populateTable()
    }

    private fun initColumns() {
        positionColumn.setCellFactory {
            object : TableCell<Player, Int>() {
                private fun highlightIfCurrentPlayer(row: TableRow<Player>) {
                    if (row.item == currentPlayer)
                        row.styleClass.add("highlight")
                }

                private fun getPosition(player: Player): Int {
                    return allPlayers.indexOf(player) + 1
                }

                override fun updateIndex(i: Int) {
                    super.updateIndex(i)

                    text = if (isEmpty || tableRow == null) {
                        ""
                    } else {
                        val player = tableRow.item ?: return

                        highlightIfCurrentPlayer(tableRow)
                        getPosition(player).toString()
                    }
                }
            }
        }
        nameColumn.cellValueFactory = PropertyValueFactory<Player, String>("name")
        scoreColumn.cellValueFactory = PropertyValueFactory<Player, Int>("score")
    }

    private fun addPlayersToTable() {
        val players = mutableSetOf<Player>().apply {
            addAll(allPlayers.take(5))
            add(currentPlayer)
        }

        playersTable.items = FXCollections.observableList(players.toList())
    }

    private fun sortTable() {
        scoreColumn.comparator = scoreColumn.comparator.reversed()
        playersTable.sortOrder.add(scoreColumn)
    }

    /**
     * Populates the table on the end screen.
     */
    private fun populateTable() {
        addPlayersToTable()
        sortTable()
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