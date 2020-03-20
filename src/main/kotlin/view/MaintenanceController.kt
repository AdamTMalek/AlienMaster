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
import javafx.scene.control.*
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import view.MaintenanceController.Companion.loadRoot
import view.MaintenanceController.Companion.showAndWait
import java.net.URL
import java.util.*

/**
 * The [MaintenanceController] is responsible for controlling all actions
 * that happen in the maintenance view of which controls are defined in the
 * fxml file maintenance_view.fxml.
 *
 * To open the view, it is recommended to use [showAndWait] and pass in
 * a reference to the [Serial] implementation.
 *
 * Mainly used testing purposes, the [loadRoot] loads the FXML file, sets up
 * the required configurations and returns the [Parent] of the view.
 * It does not however create a window.
 */
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

    @FXML
    private var distanceReading = Label()

    @FXML
    private var goodAlienStateLabel = Label()

    @FXML
    private var badAlienStateLabel = Label()

    @FXML
    private var redReading = Label()

    @FXML
    private var greenReading = Label()

    @FXML
    private var blueReading = Label()

    @FXML
    private var clearReading = Label()

    // The message parser will be used for creating messages out of incoming yaml from serial
    private val messageParser = MessageParser()
    private lateinit var serial: Serial

    companion object {
        const val BUTTON_PRESSED_STYLE = "button-pressed"
        const val LED_ACTIVE_STYLE = "led-active"
        const val GOOD_ALIEN_ID = 0
        const val BAD_ALIEN_ID = 1

        /**
         * Loads the fxml file (maintenance_view.fxml), sets up the controller
         * and returns the parent node
         *
         * @param serial Serial implementation
         * @return Parent node of the loaded fxml
         */
        fun loadRoot(serial: Serial): Parent {
            val url = this::class.java.classLoader.getResource("view/maintenance_view.fxml")
            val loader = FXMLLoader().apply { location = url }
            val root = loader.load<Parent>()

            val controller = loader.getController<MaintenanceController>()
            controller.setSerial(serial)

            return root
        }

        /**
         * Loads the fxml file (maintenance_view.fxml), sets up the controller
         * and opens a new window using [Stage.showAndWait] method.
         *
         * @param serial Serial implementation
         */
        fun showAndWait(serial: Serial) {
            val root = loadRoot(serial)

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
        addPortChoiceChangeListener()
    }

    private fun addAutoScrollToLogText() {
        logText.textProperty().addListener { _, _, _ -> logText.scrollTop = Double.MAX_VALUE }
    }

    private fun addPortChoiceChangeListener() {
        portChoice.selectionModel.selectedItemProperty().addListener { _, _, newPort ->
            if (newPort == null)
                return@addListener

            val port = serial.getAllAvailablePorts().find { it.descriptivePortName == newPort }!!
            serial.connectTo(port)
            onSerialComEstablished()
        }
    }

    /**
     * Sets the serial reference to [serial] and configures for receiving data
     */
    fun setSerial(serial: Serial) {
        this.serial = serial
        this.serial.addPortListener(this)
        this.serial.addDataReceivedListener(this)
        setupPortChoice()
    }

    /**
     * Picks the current com port in the choice box if [Serial.getActivePort] returns non-null value
     */
    private fun setupPortChoice() {
        val serialNames = getAllAvailablePortsNames()

        portChoice.items.addAll(serialNames)

        val current = serial.getActivePort()
            ?: return

        val currentPortIndex = portChoice.items.indexOf(current.descriptivePortName)
        portChoice.selectionModel.select(currentPortIndex)
    }

    private fun requestLedStates() {
        val actions = (0..5).map { Action(ActionType.GET, DeviceType.LED, it, emptyList()) }
        actions.forEach { action ->
            sendAction(action)
        }
    }

    private fun requestAlienStates() {
        val actions = (0..1).map { Action(ActionType.GET, DeviceType.SERVO, it, emptyList()) }
        actions.forEach { action ->
            sendAction(action)
        }
    }

    override fun onActionReceived(action: Action) {
        // This method may be invoked by other threads
        Platform.runLater {
            addToLogTextView(action.toYaml(), false)

            when (action.deviceType) {
                DeviceType.BUTTON -> handleButtonAction(action)
                DeviceType.CARD -> handleCardReaderAction(action)
                DeviceType.DISTANCE_SENSOR -> handleDistanceReadingAction(action)
                DeviceType.LED -> handleLedAction(action)
                DeviceType.SERVO -> handleServoAction(action)
                DeviceType.COLOUR_SENSOR -> handleColourSensorAction(action)
            }
        }
    }

    /**
     * Handles the given [action] if it is related to buttons
     *
     * @throws IllegalArgumentException if the action is not related to buttons
     */
    @Throws(IllegalArgumentException::class)
    private fun handleButtonAction(action: Action) {
        if (action.deviceType != DeviceType.BUTTON)
            throw IllegalArgumentException("Action is not a button action")

        val isPressed = action.values.first() == 1
        when (action.deviceId) {
            0 -> changeButtonState(button0, isPressed)
            1 -> changeButtonState(button1, isPressed)
            2 -> changeButtonState(button2, isPressed)
            3 -> changeButtonState(button3, isPressed)
            4 -> changeButtonState(button4, isPressed)
            5 -> changeButtonState(button5, isPressed)
        }
    }

    /**
     * Handles the given [action] if it is related to the card reader
     *
     * @throws IllegalArgumentException if the action is not related to the card reader
     */
    @Throws(IllegalArgumentException::class)
    private fun handleCardReaderAction(action: Action) {
        if (action.deviceType != DeviceType.CARD)
            throw IllegalArgumentException("Action is not a card reader action")

        val id = action.values.first()

        userIdLabel.text = if (id <= -1)
            "(card not present)"
        else
            id.toString()
    }

    /**
     * Handles the given [action] if it is related to the distance sensor
     *
     * @throws IllegalArgumentException if the action is not related to the distance sensor
     */
    @Throws(IllegalArgumentException::class)
    private fun handleDistanceReadingAction(action: Action) {
        if (action.deviceType != DeviceType.DISTANCE_SENSOR)
            throw IllegalArgumentException("Action is not a distance sensor action")

        val distance = action.values.first()
        distanceReading.text = distance.toString()
    }

    /**
     * Handles the given [action] if it is related to the LEDs
     *
     * @throws IllegalArgumentException if the action is not related to the LEDs
     */
    @Throws(IllegalArgumentException::class)
    private fun handleLedAction(action: Action) {
        if (action.deviceType != DeviceType.LED)
            throw IllegalArgumentException("Action is not a LED action")

        val led = when (action.deviceId) {
            0 -> led0
            1 -> led1
            2 -> led2
            3 -> led3
            4 -> led4
            5 -> led5
            else -> return
        }

        if (action.values.first() == 1)
            setLedActive(led)
        else
            setLedInactive(led)
    }

    /**
     * Handles the given [action] if it is related to the servos
     *
     * @throws IllegalArgumentException if the action is not related to the servos
     */
    @Throws(IllegalArgumentException::class)
    private fun handleServoAction(action: Action) {
        if (action.deviceType != DeviceType.SERVO)
            throw IllegalArgumentException("Action is not a servo action")

        val isRaised = action.values.first() == 1

        when (action.deviceId) {
            0 -> changeAlienStateLabel(goodAlienStateLabel, isRaised)
            1 -> changeAlienStateLabel(badAlienStateLabel, isRaised)
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun handleColourSensorAction(action: Action) {
        if (action.deviceType != DeviceType.COLOUR_SENSOR)
            throw IllegalArgumentException("Action is not a colour sensor action")

        val (red, green, blue, clear) = action.values
        redReading.text = "%X".format(red)
        greenReading.text = "%X".format(green)
        blueReading.text = "%X".format(blue)
        clearReading.text = "%X".format(clear)
    }

    /**
     * Handles [StateMessage] messages coming from the serial.
     * It just adds them to the log view.
     */
    override fun onStateReceived(state: StateMessage) {
        addToLogTextView(state.toYaml(), false)
    }

    override fun onAvailablePortsChange() {
        Platform.runLater {
            portChoice.items.clear()
            portChoice.items.addAll(getAllAvailablePortsNames())
        }
    }

    /**
     * Returns a list of descriptive port names of the available ports
     */
    private fun getAllAvailablePortsNames() = serial.getAllAvailablePorts().map { it.descriptivePortName }

    fun onLed0Clicked() {
        onLedClicked(0, led0)
    }

    fun onLed1Clicked() {
        onLedClicked(1, led1)
    }

    fun onLed2Clicked() {
        onLedClicked(2, led2)
    }

    fun onLed3Clicked() {
        onLedClicked(3, led3)
    }

    fun onLed4Clicked() {
        onLedClicked(4, led4)
    }

    fun onLed5Clicked() {
        onLedClicked(5, led5)
    }

    private fun onLedClicked(id: Int, led: Styleable) {
        try {
            sendSetLedAction(id, !isLedActive(led))
            toggleLedActive(led)
        } catch (ex: IllegalStateException) {
            displaySerialNotConnectedError()
        }
    }

    /**
     * Returns true if the led is active in the view
     * (i.e. when the led has [LED_ACTIVE_STYLE] class)
     */
    private fun isLedActive(led: Styleable): Boolean {
        return led.styleClass.contains(LED_ACTIVE_STYLE)
    }

    /**
     * Adds or removes [LED_ACTIVE_STYLE] to the led
     * depending on the previous state
     */
    private fun toggleLedActive(led: Styleable) {
        if (isLedActive(led))
            setLedInactive(led)
        else
            setLedActive(led)
    }

    private fun setLedActive(led: Styleable) {
        led.styleClass.add(LED_ACTIVE_STYLE)
    }

    private fun setLedInactive(led: Styleable) {
        led.styleClass.remove(LED_ACTIVE_STYLE)
    }

    /**
     * Sends the set [Action] to the serial and logs the message
     * @param id ID of the LED
     * @param on true if the LED is to be turned on, false otherwise
     */
    private fun sendSetLedAction(id: Int, on: Boolean) {
        val value = if (on) 1 else 0
        val action = Action(ActionType.SET, DeviceType.LED, id, listOf(value))
        sendAction(action)
    }

    fun raiseGoodAlien() {
        changeAlienStateLabel(goodAlienStateLabel, true)
        moveAlien(GOOD_ALIEN_ID, true)
    }

    fun lowerGoodAlien() {
        changeAlienStateLabel(goodAlienStateLabel, false)
        moveAlien(GOOD_ALIEN_ID, false)
    }

    fun raiseBadAlien() {
        changeAlienStateLabel(badAlienStateLabel, true)
        moveAlien(BAD_ALIEN_ID, true)
    }

    fun lowerBadAlien() {
        changeAlienStateLabel(badAlienStateLabel, false)
        moveAlien(BAD_ALIEN_ID, false)
    }

    private fun changeAlienStateLabel(label: Label, raised: Boolean) {
        label.text = if (raised) "raised" else "lowered"
    }

    private fun moveAlien(alienId: Int, raise: Boolean) {
        val value = if (raise) 1 else 0
        val action = Action(ActionType.SET, DeviceType.SERVO, alienId, listOf(value))

        try {
            sendAction(action)
        } catch (ex: IllegalStateException) {
            displaySerialNotConnectedError()
        }
    }

    fun requestColourReading() {
        val action = Action(ActionType.GET, DeviceType.COLOUR_SENSOR, 0, emptyList())

        try {
            sendAction(action)
        } catch (ex: IllegalStateException) {
            displaySerialNotConnectedError()
        }
    }

    fun requestDistanceSensorReading() {
        val action = Action(ActionType.GET, DeviceType.DISTANCE_SENSOR, 0, emptyList())

        try {
            sendAction(action)
        } catch (ex: IllegalStateException) {
            displaySerialNotConnectedError()
        }
    }

    /**
     * Sends [Action] to the serial and adds it to the log view
     */
    private fun sendAction(action: Action) {
        val data = action.toYaml()

        serial.send(data)
        addToLogTextView(data, true)
    }

    private fun displaySerialNotConnectedError() {
        Alert(Alert.AlertType.ERROR, "Please connect to a serial port first", ButtonType.OK).apply {
            title = "Serial Communication Error"
            showAndWait()
        }
    }

    /**
     * Add [BUTTON_PRESSED_STYLE] to the [button] if [pressed] is true.
     * Otherwise remove the style.
     */
    private fun changeButtonState(button: Styleable, pressed: Boolean) {
        if (pressed)
            button.styleClass.add(BUTTON_PRESSED_STYLE)
        else
            button.styleClass.removeIf { it == BUTTON_PRESSED_STYLE }
    }

    /**
     * Invoked after connecting to a serial port
     */
    private fun onSerialComEstablished() {
        sendDebugMessage()
        requestLedStates()
        requestAlienStates()
    }

    private fun sendDebugMessage() {
        val message = "{\n\tdebug: 1\n}"
        serial.send(message)
        addToLogTextView(message, true)
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

        logText.appendText(directionChar + indentedText + "\n")
    }

    override fun onDataReceived(data: String) {
        messageParser.parse(data)
    }
}