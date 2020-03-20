package view

import app.serialcom.*
import com.fazecast.jSerialComm.SerialPort
import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.stage.Stage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.fail
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.ApplicationTest

@ExtendWith(ApplicationExtension::class)
class MaintenanceControllerTest : ApplicationTest() {
    private val serial = object : Serial {
        val dataListeners = mutableListOf<OnSerialDataReceivedListener>()
        val portListeners = mutableListOf<OnAvailablePortsChangeListener>()
        var lastMessage: String? = null

        override fun getAllAvailablePorts(): Collection<SerialPort> {
            return emptyList()
        }

        override fun getActivePort(): SerialPort? {
            return null
        }

        override fun connectTo(port: SerialPort) {

        }

        override fun isConnected(): Boolean {
            return true
        }

        override fun addDataReceivedListener(listener: OnSerialDataReceivedListener) {
            dataListeners.add(listener)
        }

        override fun removeDataReceivedListener(listener: OnSerialDataReceivedListener) {
            dataListeners.remove(listener)
        }

        override fun addPortListener(listener: OnAvailablePortsChangeListener) {
            portListeners.add(listener)
        }

        override fun removePortListener(listener: OnAvailablePortsChangeListener) {
            portListeners.remove(listener)
        }

        override fun send(data: String) {
            lastMessage = data
        }
    }

    override fun start(stage: Stage) {
        val root = MaintenanceController.loadRoot(serial)
        val scene = Scene(root, 900.0, 800.0)
        stage.scene = scene
        stage.show()
    }

    private fun getLog() = lookup("#logText").tryQuery<TextArea>().get().text

    @Test
    fun testAddsSelfAsPortListener() {
        assertTrue(serial.portListeners.isNotEmpty())
    }

    @Test
    fun testAddsSelfAsDataListener() {
        assertTrue(serial.dataListeners.isNotEmpty())
    }

    @Test
    fun testAllLedActivate() {
        Thread.sleep(10) // Wait for the window to show up

        val ids = (0..5).map { "#led$it" }

        ids.forEach { id ->
            val led = lookup(id).tryQuery<Node>().get()

            if (led.styleClass.contains("led-active"))
                fail("LEDs should be active by default")

            clickOn(led)
            assertTrue(led.styleClass.contains("led-active"))
        }
    }

    @Test
    fun testAllLedDeactivate() {
        Thread.sleep(10) // Wait for the window to show up

        val ids = (0..5).map { "#led$it" }
        ids.forEach { id ->
            val led = lookup(id).tryQuery<Node>().get()

            clickOn(led)
            clickOn(led)

            assertFalse(led.styleClass.contains("led-active"))
        }
    }

    @Test
    fun testSetLedActionIsLogged() {
        Thread.sleep(10) // Wait for the window to show up

        val deviceIds = (0..5).map { "led$it" }

        deviceIds.forEach { id ->
            val led = lookup("#$id").tryQuery<Node>().get()

            val expectedActionText = Regex("action: set\\s+device: ${id.toUpperCase()}\\s+value: 1")

            clickOn(led)
            val log = getLog()
            assertTrue(log.contains(expectedActionText))
        }
    }

    @Test
    fun testLedActionIsSentToSerial() {
        Thread.sleep(10) // Wait for the window to show up

        val deviceIds = (0..5)

        deviceIds.forEach { id ->
            val led = lookup("#led$id").tryQuery<Node>().get()

            val expectedTurnOnActionText = Action(ActionType.SET, DeviceType.LED, id, listOf(1)).toYaml()
            clickOn(led)
            assertEquals(expectedTurnOnActionText, serial.lastMessage)

            val expectedTurnOffActionText = Action(ActionType.SET, DeviceType.LED, id, listOf(0)).toYaml()
            clickOn(led)
            assertEquals(expectedTurnOffActionText, serial.lastMessage)
        }
    }

    @Test
    fun testRaiseGoodAlienActionIsLogged() {
        Thread.sleep(10) // Wait for the window to show up
        val button = lookup("#raiseGoodAlienButton").tryQuery<Node>().get()

        clickOn(button)

        val expectedLog = Regex("action: set\\s+device: SRV0\\s+value: 1")
        val log = getLog()
        assertTrue(log.contains(expectedLog))
    }

    @Test
    fun testLowerGoodAlienActionIsLogged() {
        Thread.sleep(10) // Wait for the window to show up
        val button = lookup("#lowerGoodAlienButton").tryQuery<Node>().get()

        clickOn(button)

        val expectedLog = Regex("action: set\\s+device: SRV0\\s+value: 0")
        val log = getLog()
        assertTrue(log.contains(expectedLog))
    }

    @Test
    fun testRaiseBadAlienActionIsLogged() {
        Thread.sleep(10) // Wait for the window to show up
        val button = lookup("#raiseBadAlienButton").tryQuery<Node>().get()

        clickOn(button)

        val expectedLog = Regex("action: set\\s+device: SRV1\\s+value: 1")
        val log = getLog()
        assertTrue(log.contains(expectedLog))
    }

    @Test
    fun testLowerBadAlienActionIsLogged() {
        Thread.sleep(10) // Wait for the window to show up
        val button = lookup("#lowerBadAlienButton").tryQuery<Node>().get()

        clickOn(button)

        val expectedLog = Regex("action: set\\s+device: SRV1\\s+value: 0")
        val log = getLog()
        assertTrue(log.contains(expectedLog))
    }

    @Test
    fun testRaiseGoodAlienIsSentToSerial() {
        Thread.sleep(10) // Wait for the window to show up
        val button = lookup("#raiseGoodAlienButton").tryQuery<Node>().get()

        clickOn(button)

        val expectedActionText = Action(ActionType.SET, DeviceType.SERVO, 0, listOf(1)).toYaml()
        assertEquals(expectedActionText, serial.lastMessage)
    }

    @Test
    fun testLowerGoodAlienIsSentToSerial() {
        Thread.sleep(10) // Wait for the window to show up
        val button = lookup("#lowerGoodAlienButton").tryQuery<Node>().get()

        clickOn(button)

        val expectedActionText = Action(ActionType.SET, DeviceType.SERVO, 0, listOf(0)).toYaml()
        assertEquals(expectedActionText, serial.lastMessage)
    }

    @Test
    fun testRaiseBadAlienIsSentToSerial() {
        Thread.sleep(10) // Wait for the window to show up
        val button = lookup("#raiseBadAlienButton").tryQuery<Node>().get()

        clickOn(button)

        val expectedActionText = Action(ActionType.SET, DeviceType.SERVO, 1, listOf(1)).toYaml()
        assertEquals(expectedActionText, serial.lastMessage)
    }

    @Test
    fun testLowerBadAlienIsSentToSerial() {
        Thread.sleep(10) // Wait for the window to show up
        val button = lookup("#lowerBadAlienButton").tryQuery<Node>().get()

        clickOn(button)

        val expectedActionText = Action(ActionType.SET, DeviceType.SERVO, 1, listOf(0)).toYaml()
        assertEquals(expectedActionText, serial.lastMessage)
    }

    @Test
    fun testCardIdIsUpdatedWithCardPresent() {
        Thread.sleep(10) // Wait for the window to show up
        val userIdLabel = lookup("#userIdLabel").tryQuery<Label>().get()
        val receivedAction = Action(ActionType.REPORT, DeviceType.CARD, 0, listOf(15)).toYaml()

        serial.dataListeners.first().onDataReceived(receivedAction)

        Thread.sleep(100) // Wait for the Platform.runLater to execute
        assertEquals("15", userIdLabel.text)
    }

    @Test
    fun testCardIdIsUpdatedWithNoCard() {
        val userIdLabel = lookup("#userIdLabel").tryQuery<Label>().get()
        Platform.runLater {
            userIdLabel.text = "" // Change to some arbitrary value, as if the card was present
        }

        Thread.sleep(100) // Wait for the window to show up and for the Platform.runLater to execute


        val receivedAction = Action(ActionType.REPORT, DeviceType.CARD, 0, listOf(-1)).toYaml()

        serial.dataListeners.first().onDataReceived(receivedAction)

        Thread.sleep(100) // Wait for the Platform.runLater to execute
        assertTrue(userIdLabel.text.contains("card not present"))
    }

    @Test
    fun testRequestReadingIsLogged() {
        Thread.sleep(10) // Wait for the window to show up
        val button = lookup("#requestDistanceButton").tryQuery<Node>().get()

        clickOn(button)

        val expectedText = Regex("action: get\\s+device: DST0")
        val log = getLog()
        assertTrue(log.contains(expectedText))
    }

    @Test
    fun testRequestReadingIsSentToSerial() {
        Thread.sleep(10) // Wait for the window to show up
        val button = lookup("#requestDistanceButton").tryQuery<Node>().get()

        clickOn(button)

        val expectedText = Action(ActionType.GET, DeviceType.DISTANCE_SENSOR, 0, emptyList()).toYaml()
        assertEquals(expectedText, serial.lastMessage)
    }

    @Test
    fun testDistanceIsSet() {
        Thread.sleep(10) // Wait for the window to show up
        val distanceLabel = lookup("#distanceReading").tryQuery<Label>().get()
        val incomingAction = Action(ActionType.REPORT, DeviceType.DISTANCE_SENSOR, 0, listOf(6)).toYaml()
        serial.dataListeners.first().onDataReceived(incomingAction)

        Thread.sleep(100) // Wait for the Platform.runLater to execute
        assertEquals("6", distanceLabel.text)
    }

    @Test
    fun testIncomingActionIsLogged() {
        val incomingAction = Action(ActionType.REPORT, DeviceType.DISTANCE_SENSOR, 0, listOf(6)).toYaml()
        serial.dataListeners.first().onDataReceived(incomingAction)

        val expectedText = Regex("action: report\\s+device: DST0\\s+value: 6")

        Thread.sleep(100) // Wait for the Platform.runLater to execute
        val log = getLog()

        assertTrue(log.isNotEmpty())
        assertTrue(log.contains(expectedText))
    }

    @Test
    fun testButtonsGetPressedStyleWhenReportReceived() {
        val ids = (0..5)
        ids.forEach { id ->
            val incomingAction = Action(ActionType.REPORT, DeviceType.BUTTON, id, listOf(1)).toYaml()
            serial.dataListeners.first().onDataReceived(incomingAction)

            val button = lookup("#button$id").tryQuery<Node>().get()

            Thread.sleep(100)
            assertTrue(button.styleClass.contains("button-pressed"))
        }
    }

    @Test
    fun testButtonsGetPressedStyleRemoved() {
        val ids = (0..5)
        ids.forEach { id ->
            val pressedAction = Action(ActionType.REPORT, DeviceType.BUTTON, id, listOf(1)).toYaml()
            serial.dataListeners.first().onDataReceived(pressedAction)

            val unpressedAction = Action(ActionType.REPORT, DeviceType.BUTTON, id, listOf(0)).toYaml()
            serial.dataListeners.first().onDataReceived(unpressedAction)

            val button = lookup("#button$id").tryQuery<Node>().get()

            Thread.sleep(100)
            assertFalse(button.styleClass.contains("button-pressed"))
        }
    }

    @Test
    fun testLedActivatesWhenReportReceived() {
        val actions = (0..5).map { Action(ActionType.REPORT, DeviceType.LED, it, listOf(1)).toYaml() }
        actions.forEachIndexed { index, action ->
            serial.dataListeners.first().onDataReceived(action)

            Thread.sleep(100)
            val led = lookup("#led$index").tryQuery<Node>().get()
            assertTrue(led.styleClass.contains(MaintenanceController.LED_ACTIVE_STYLE))
        }
    }

    @Test
    fun testLedDeactivatesWhenReportReceived() {
        // Prepare (add active style to all leds)
        val leds = (0..5).map { "#led$it" }.map { id ->
            lookup(id).tryQuery<Node>().get()
        }
        leds.forEach { it.styleClass.add(MaintenanceController.LED_ACTIVE_STYLE) }

        // Test
        val actions = (0..5).map { Action(ActionType.REPORT, DeviceType.LED, it, listOf(0)).toYaml() }
        actions.forEachIndexed { index, action ->
            serial.dataListeners.first().onDataReceived(action)

            Thread.sleep(100)
            assertFalse(leds[index].styleClass.contains(MaintenanceController.LED_ACTIVE_STYLE))
        }
    }

    @Test
    fun testAlienStatesAreUnknownByDefault() {
        val labels = listOf(
            lookup("#goodAlienStateLabel").tryQuery<Label>().get(),
            lookup("#badAlienStateLabel").tryQuery<Label>().get()
        )

        labels.forEach { label ->
            assertEquals("unknown", label.text)
        }
    }

    @Test
    fun testGoodAlienStateWhenRaised() {
        val action = Action(ActionType.REPORT, DeviceType.SERVO, 0, listOf(1)).toYaml()
        serial.dataListeners.first().onDataReceived(action)

        Thread.sleep(100)
        val label = lookup("#goodAlienStateLabel").tryQuery<Label>().get()
        assertEquals("raised", label.text)
    }

    @Test
    fun testGoodAlienStateWhenLowered() {
        val action = Action(ActionType.REPORT, DeviceType.SERVO, 0, listOf(0)).toYaml()
        serial.dataListeners.first().onDataReceived(action)

        Thread.sleep(100)
        val label = lookup("#goodAlienStateLabel").tryQuery<Label>().get()
        assertEquals("lowered", label.text)
    }

    @Test
    fun testBadAlienStateWhenRaised() {
        val action = Action(ActionType.REPORT, DeviceType.SERVO, 1, listOf(1)).toYaml()
        serial.dataListeners.first().onDataReceived(action)

        Thread.sleep(100)
        val label = lookup("#badAlienStateLabel").tryQuery<Label>().get()
        assertEquals("raised", label.text)
    }

    @Test
    fun testBadAlienStateWhenLowered() {
        val action = Action(ActionType.REPORT, DeviceType.SERVO, 1, listOf(0)).toYaml()
        serial.dataListeners.first().onDataReceived(action)

        Thread.sleep(100)
        val label = lookup("#badAlienStateLabel").tryQuery<Label>().get()
        assertEquals("lowered", label.text)
    }

    @Test
    fun testRaiseGoodAlienChangesStateLabel() {
        val button = lookup("#raiseGoodAlienButton").tryQuery<Node>().get()
        clickOn(button)

        val label = lookup("#goodAlienStateLabel").tryQuery<Label>().get()
        assertEquals("raised", label.text)
    }

    @Test
    fun testLowerGoodAlienChangesStateLabel() {
        val button = lookup("#lowerGoodAlienButton").tryQuery<Node>().get()
        clickOn(button)

        val label = lookup("#goodAlienStateLabel").tryQuery<Label>().get()
        assertEquals("lowered", label.text)
    }

    @Test
    fun testRaiseBadAlienChangesState() {
        val button = lookup("#raiseBadAlienButton").tryQuery<Node>().get()
        clickOn(button)

        val label = lookup("#badAlienStateLabel").tryQuery<Label>().get()
        assertEquals("raised", label.text)
    }

    @Test
    fun testLowerBadAlienChangesState() {
        val button = lookup("#lowerBadAlienButton").tryQuery<Node>().get()
        clickOn(button)

        val label = lookup("#badAlienStateLabel").tryQuery<Label>().get()
        assertEquals("lowered", label.text)
    }

    @Test
    fun testColourParsedCorrectly() {
        val red = 0xFFFF
        val green = 0xDDDD
        val blue = 0xBBBB
        val clear = 0x9999
        val action = Action(ActionType.REPORT, DeviceType.COLOUR_SENSOR, 0, listOf(red, green, blue, clear)).toYaml()
        serial.dataListeners.first().onDataReceived(action)

        Thread.sleep(100)

        val redLabel = lookup("#redReading").tryQuery<Label>().get()
        val greenLabel = lookup("#greenReading").tryQuery<Label>().get()
        val blueLabel = lookup("#blueReading").tryQuery<Label>().get()
        val clearLabel = lookup("#clearReading").tryQuery<Label>().get()

        assertEquals("%X".format(red), redLabel.text)
        assertEquals("%X".format(green), greenLabel.text)
        assertEquals("%X".format(blue), blueLabel.text)
        assertEquals("%X".format(clear), clearLabel.text)
    }

    @Test
    fun testSendsColourReadingRequest() {
        Thread.sleep(10) // Wait for the window to show up
        val button = lookup("#requestColourButton").tryQuery<Node>().get()

        clickOn(button)

        val expectedActionText = Action(ActionType.GET, DeviceType.COLOUR_SENSOR, 0, emptyList()).toYaml()
        assertEquals(expectedActionText, serial.lastMessage)
    }
}