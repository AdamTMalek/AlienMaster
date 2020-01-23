package view

import app.Players
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
    private var playersTable = TableView<Players>()
    @FXML
    private var idColumn = TableColumn<Players, Int>()
    @FXML
    private var nameColumn = TableColumn<Players, String>()
    @FXML
    private var scoreColumn = TableColumn<Players, Int>()

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
        idColumn.cellValueFactory = PropertyValueFactory<Players, Int>("id")
        nameColumn.cellValueFactory = PropertyValueFactory<Players, String>("name")
        scoreColumn.cellValueFactory = PropertyValueFactory<Players, Int>("score")
    }


    fun close() {

    }

    fun addPlayer() {

    }
}