package view

import app.IPlayer
import app.Language
import app.Player
import app.tts.TextToSpeech
import extensions.getParameterizedString
import javafx.animation.Animation
import javafx.animation.FadeTransition
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.layout.Pane
import javafx.scene.media.MediaView
import javafx.util.Duration
import java.net.URL
import java.util.*

/**
 * The [WelcomeScreenController] controls the welcome_screen.fxml
 * that is displayed when the player has inserted their card
 * and the player object has been read from the database.
 *
 * The view will playback an animation of the game controller
 * and it will display instructions in the player's chosen
 * language underneath the video
 */
class WelcomeScreenController : Initializable {
    companion object {
        private lateinit var player: IPlayer
        private val playerLanguage by lazy { Language.fromCode(player.language) }

        fun loadWithAnimation(root: Pane, player: IPlayer) {
            this.player = player
            val loader = loadFxml()
            val parent = loader.load<Parent>()
            LoadAnimation.loadWithAnimation(root, parent)
        }

        private fun loadFxml(): FXMLLoader {
            val resource = this::class.java.classLoader.getResource("view/welcome_screen.fxml")
            return FXMLLoader().apply { location = resource }
        }
    }

    @FXML
    private var mediaView = MediaView()
    @FXML
    private var promptLabel = Label()

    private val tts = TextToSpeech()

    /**
     * The [textResourceBundle] contains all the locale-dependent translations of text
     * appearing in the view.
     */
    private val textResourceBundle: ResourceBundle by lazy {
        val language = Language.fromCode(player.language)
        ResourceBundle.getBundle("TextBundle", language.locale)
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        enableMediaPlayerRepeat()
        startInstructionsReading()
        addPulsingToPromptLabel()
    }

    /**
     * Loops the animation video forever
     */
    private fun enableMediaPlayerRepeat() {
        mediaView.mediaPlayer.setOnEndOfMedia {
            with(mediaView.mediaPlayer) {
                seek(Duration.ZERO)
                play()
            }
        }
    }

    fun getGreetingText(): String = textResourceBundle.getParameterizedString("welcome_screen.greeting", player.name)

    fun getZerothInstructionText(): String = textResourceBundle.getString("instructions.zeroth")

    fun getFirstInstructionText(): String = textResourceBundle.getString("instructions.first")

    fun getSecondInstructionText(): String = textResourceBundle.getString("instructions.second")

    fun getThirdInstructionText(): String = textResourceBundle.getString("instructions.third")

    fun getFourthInstructionText(): String = textResourceBundle.getString("instructions.fourth")

    fun getFinalInstructionText(): String = textResourceBundle.getString("instructions.final")

    fun getPromptText(): String = textResourceBundle.getString("instructions.prompt")

    /**
     * Adds the instructions to the TTS queue
     */
    private fun startInstructionsReading() {
        with(tts) {
            say(playerLanguage, getZerothInstructionText())
            say(playerLanguage, getFirstInstructionText())
            say(playerLanguage, getSecondInstructionText())
            say(playerLanguage, getThirdInstructionText())
            say(playerLanguage, getFourthInstructionText())
            say(playerLanguage, getFinalInstructionText())
        }
    }

    /**
     * Adds pulsation to the prompt label
     */
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