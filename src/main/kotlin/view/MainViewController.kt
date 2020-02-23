package view

import app.Language
import app.Player
import app.PlayersDatabase
import app.serialcom.Serial
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.input.KeyCode
import javafx.scene.layout.Pane
import javafx.stage.Stage
import java.net.URL
import java.util.*

class MainViewController : Initializable {
    @FXML
    private var rootPane = Pane()

    private lateinit var serial: Serial

    override fun initialize(location: URL?, resources: ResourceBundle?) {
    }

    fun setSerial(serial: Serial) {
        this.serial = serial
    }

    /**
     * TODO: Delete after testing
     */
    fun setStage() {
        rootPane.scene.setOnKeyPressed { key ->
            val player = when (key.code) {
                KeyCode.G -> getGermanPlayer()
                KeyCode.E -> getEnglishPlayer()
                KeyCode.L -> getLastPlayer()
                KeyCode.C -> {
                    SplashScreenController.loadWithAnimation(rootPane); return@setOnKeyPressed
                }
                KeyCode.W -> {
                    val player = getGermanPlayer()
                    WelcomeScreenController.loadWithAnimation(rootPane, player); return@setOnKeyPressed
                }
                else -> return@setOnKeyPressed
            }

            val isNewTopScore = key.isShiftDown
            loadEndScreen(player, isNewTopScore)
        }
    }

    /**
     * TODO: Delete after testing
     */
    private fun getGermanPlayer(): Player {
        return PlayersDatabase.getAllPlayers().find { it.language == Language.GER.code }!!
    }

    /**
     * TODO: Delete after testing
     */
    private fun getEnglishPlayer(): Player {
        return PlayersDatabase.getAllPlayers().find { it.language == Language.ENG.code }!!
    }

    /**
     * TODO: Delete after testing
     */
    private fun getLastPlayer(): Player {
        return PlayersDatabase.getAllPlayers().minBy { it.score }!!
    }

    private fun loadEndScreen(player: Player, isNewTopScore: Boolean) {
        EndScreenController.loadWithAnimation(rootPane, player, 1, isNewTopScore)
    }

    fun openMaintenanceWindow() {
        MaintenanceController.showAndWait(serial)
    }

    fun openPlayersEditor() {
        EditorViewController.showView()
    }

    fun close() {
        val stage = rootPane.scene.window as Stage
        stage.close()
    }
}