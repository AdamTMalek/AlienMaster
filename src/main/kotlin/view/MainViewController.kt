package view

import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import java.net.URL
import java.util.*

class MainViewController : Initializable {
    @FXML
    private var portChoice = ChoiceBox<String>()
    @FXML
    private var userInput = TextField()
    @FXML
    private var dataReceived = TextArea()

    val ports = FXCollections.observableArrayList<String>()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        portChoice.items = ports
    }

    fun sendData() {

    }
}