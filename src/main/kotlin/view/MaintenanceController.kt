package view

import app.MessageParser
import app.OnMessageReceivedListener
import app.serialcom.*
import javafx.application.Platform
import javafx.css.Styleable
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import java.net.URL
import java.util.*

class MaintenanceController : Initializable, OnMessageReceivedListener, OnAvailablePortsChangeListener,
    OnSerialDataReceivedListener {

    @FXML
    private var portChoice = ChoiceBox<String>()
    @FXML
    private var button0 = Rectangle()
    @FXML
    private var led0 = Circle()
    @FXML
    private var button1 = Rectangle()
    @FXML
    private var led1 = Circle()
    @FXML
    private var button2 = Rectangle()
    @FXML
    private var led2 = Circle()
    @FXML
    private var button3 = Rectangle()
    @FXML
    private var led3 = Circle()
    @FXML
    private var button4 = Rectangle()
    @FXML
    private var led4 = Circle()
    @FXML
    private var button5 = Rectangle()
    @FXML
    private var led5 = Circle()
    @FXML
    private var userIdLabel = Label()
    @FXML
    private var logText = TextArea()

    private val buttonPressedStyle = "button-pressed"
    private val ledActiveStyle = "led-active"
    private val goodAlienId = 0
    private val badAlienId = 1
    private val messageParser = MessageParser()

    private lateinit var serial: Serial

    companion object {
        fun showAndWait(serial: Serial) {
            val url = this::class.java.classLoader.getResource("view/maintenance_view.fxml")
            val loader = FXMLLoader().apply { location = url }
            val root = loader.load<Parent>()

            val controller = loader.getController<MaintenanceController>()
            controller.setSerial(serial)

            val scene = Scene(root, 900.0, 700.0)
            Stage().apply {
                this.scene = scene
                this.title = "Maintenance Window"
                this.showAndWait()
            }
        }
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        messageParser.addListener(this)
        addAutoScrollToLogText()
    }

    private fun addAutoScrollToLogText() {
        logText.textProperty().addListener { _, _, _ -> logText.scrollTop = Double.MAX_VALUE }
    }

    fun setSerial(serial: Serial) {
        this.serial = serial
        this.serial.addPortListener(this)
        setupPortChoice()
    }

    private fun setupPortChoice() {
        val serialNames = getAllAvailablePortsNames()

        portChoice.items.addAll(serialNames)

        val current = serial.getActivePort()
            ?: return

        val currentPortIndex = portChoice.items.indexOf(current.descriptivePortName)
        portChoice.selectionModel.select(currentPortIndex)
    }

    override fun onActionReceived(action: Action) {
        addToLogTextView(action.toYaml(), false)

        when (action.deviceType) {
            DeviceType.BUTTON -> handleButtonAction(action)
            DeviceType.CARD -> handleCardReaderAction(action)
            else -> return
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun handleButtonAction(action: Action) {
        if (action.deviceType != DeviceType.BUTTON)
            throw IllegalArgumentException("Action is not a button action")

        val isPressed = action.value!! == 1
        when (action.deviceId) {
            0 -> changeButtonState(button0, isPressed)
            1 -> changeButtonState(button1, isPressed)
            2 -> changeButtonState(button2, isPressed)
            3 -> changeButtonState(button3, isPressed)
            4 -> changeButtonState(button4, isPressed)
            5 -> changeButtonState(button5, isPressed)
        }
    }

    private fun handleCardReaderAction(action: Action) {
        if (action.deviceType != DeviceType.CARD)
            throw IllegalArgumentException("Action is not a card reader action")

        val id = action.value!!

        userIdLabel.text = if (id < 0)
            "(card not present)"
        else
            id.toString()
    }

    override fun onStateReceived(state: StateMessage) {
        addToLogTextView(state.toYaml(), false)
    }

    override fun onAvailablePortsChange() {
        Platform.runLater {
            portChoice.items.clear()
            portChoice.items.addAll(getAllAvailablePortsNames())
        }
        println("Ports updated")
    }

    private fun getAllAvailablePortsNames() = serial.getAllAvailablePorts().map { it.descriptivePortName }

    fun onLed0Clicked() {
        sendSetLedAction(0, !isLedActive(led0))
        toggleLedActive(led0)
    }

    fun onLed1Clicked() {
        sendSetLedAction(1, !isLedActive(led1))
        toggleLedActive(led1)
    }

    fun onLed2Clicked() {
        sendSetLedAction(2, !isLedActive(led2))
        toggleLedActive(led2)
    }

    fun onLed3Clicked() {
        sendSetLedAction(3, !isLedActive(led3))
        toggleLedActive(led3)
    }

    fun onLed4Clicked() {
        sendSetLedAction(4, !isLedActive(led4))
        toggleLedActive(led4)
    }

    fun onLed5Clicked() {
        sendSetLedAction(5, !isLedActive(led5))
        toggleLedActive(led5)
    }

    private fun isLedActive(led: Styleable): Boolean {
        return led.styleClass.contains(ledActiveStyle)
    }

    private fun toggleLedActive(led: Styleable) {
        if (isLedActive(led))
            led.styleClass.remove(ledActiveStyle)
        else
            led.styleClass.add(ledActiveStyle)
    }

    private fun sendSetLedAction(id: Int, on: Boolean) {
        val value = if (on) 1 else 0
        val action = Action(ActionType.SET, DeviceType.LED, id, value)
        sendAction(action)
    }

    fun raiseGoodAlien() {
        val action = Action(ActionType.SET, DeviceType.SERVO, goodAlienId, 1)
        sendAction(action)
    }

    fun lowerGoodAlien() {
        val action = Action(ActionType.SET, DeviceType.SERVO, goodAlienId, 0)
        sendAction(action)
    }

    fun raiseBadAlien() {
        val action = Action(ActionType.SET, DeviceType.SERVO, badAlienId, 1)
        sendAction(action)
    }

    fun lowerBadAlien() {
        val action = Action(ActionType.SET, DeviceType.SERVO, badAlienId, 0)
        sendAction(action)
    }

    private fun sendAction(action: Action) {
        val data = action.toYaml()
        //serial.send(data) TODO: Uncomment after finished implementing the controller
        addToLogTextView(data, true)
    }

    private fun changeButtonState(button: Styleable, pressed: Boolean) {
        if (pressed)
            button.styleClass.add(buttonPressedStyle)
        else
            button.styleClass.remove(buttonPressedStyle)
    }

    /**
     * Adds the given [text] to the serial communication log view,
     * if [sent] is set to true, the text will be prepended with >
     * to indicate the direction of the message, otherwise it will
     * be prepended with <
     */
    private fun addToLogTextView(text: String, sent: Boolean) {
        val directionChar = if (sent) '>' else '<'
        val indentedText = text.prependIndent("\t")

        logText.appendText(directionChar + indentedText + "\n\n")
    }

    override fun onDataReceived(data: String) {
        messageParser.parse(data)
    }
}