package view

import app.OnSerialDataReceivedListener
import com.fazecast.jSerialComm.SerialPort
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import java.net.URL
import java.util.*

class MainViewController : Initializable, OnSerialDataReceivedListener, ChangeListener<SerialPort> {
    @FXML
    private var portChoice = ChoiceBox<SerialPort>()
    @FXML
    private var userInput = TextField()
    @FXML
    private var dataReceived = TextArea()

    private lateinit var handler: SerialCommController

    val ports = FXCollections.observableArrayList<SerialPort>()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        portChoice.items = ports
        portChoice.selectionModel.selectedItemProperty().addListener(this)
    }

    fun sendData() {
        val data = userInput.text
        dataReceived.text += "> $data\n"
        handler.sendData(data)
    }

    fun setHandler(handler: SerialCommController) {
        this.handler = handler
    }

    override fun onDataReceived(data: String) {
        dataReceived.text += "< $data\n"
    }

    override fun changed(observable: ObservableValue<out SerialPort>?, oldValue: SerialPort?, newValue: SerialPort?) {
        val port = newValue ?: return
        handler.connectToPort(port)
    }
}