package app.tts

import app.Language
import marytts.LocalMaryInterface
import marytts.util.data.audio.AudioPlayer
import java.util.*
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.LineEvent
import javax.sound.sampled.LineListener

/**
 * The [TextToSpeech] class acts as an interface to the actual library used for
 * doing text-to-speech using MaryTTS.
 *
 * Use the [say] method to use the text-to-speech.
 */
class TextToSpeech : LineListener {
    companion object {
        const val GERMAN_VOICE = "bits1-hsmm"
        const val ENGLISH_VOICE = "dfki-poppy-hsmm"

        fun getAllAvailableLocales() = LocalMaryInterface().availableLocales

        fun getAllAvailableVoices() = LocalMaryInterface().availableVoices
    }

    private val mary = LocalMaryInterface().apply { locale = getAllAvailableLocales().first() }

    // The AudioPlayer must be a variable due to implementation limitations.
    // Audio cannot be changed after it is played, we have to create new
    // player for each audio track.
    // Otherwise we get IllegalStateException.
    private lateinit var audioPlayer: AudioPlayer

    // When trying to play multiple audio tracks using the same player,
    // we will play the first one and store the rest in the queue.
    private var audioQueue: Queue<AudioInputStream> = LinkedList<AudioInputStream>()

    private var currentLineEvent = LineEvent.Type.OPEN

    init {
        initialiseAudioPlayer()
    }

    private fun initialiseAudioPlayer() {
        audioPlayer = AudioPlayer(mary.generateAudio("ignore"), this)
    }

    /**
     * Initialise MaryTTS for the given language.
     */
    private fun initialiseMary(language: Language) {
        when (language) {
            Language.ENG -> initialiseMary(Locale.UK, ENGLISH_VOICE)
            Language.GER -> initialiseMary(Locale.GERMAN, GERMAN_VOICE)
        }
    }

    /**
     * Initialise MaryTTS with the given locale and voice.
     * This method is the one that actually configures Mary,
     * but because it requires both locale and voice as string,
     * it is advised to use the method [initialiseMary] that takes
     * [Language] as a parameter.
     */
    private fun initialiseMary(locale: Locale, voice: String) {
        mary.let {
            it.locale = locale
            it.voice = voice
        }
    }

    /**
     * By the [LineListener] interface we will get updates when the
     * [audioPlayer] changes state (e.g. starts playing, stops playing etc.)
     * When the playback has finished we will create another audioPlayer
     * (refer to the comment above [audioPlayer] for the reason why that is)
     * and play back the next audio in the queue.
     */
    override fun update(event: LineEvent) {
        currentLineEvent = event.type

        if (currentLineEvent == LineEvent.Type.CLOSE) {
            if (audioQueue.isEmpty())
                return

            val audio = audioQueue.remove()

            initialiseAudioPlayer()
            playAudio(audio)
        }
    }

    /**
     * Say the given [text] in the given [language]
     */
    fun say(language: Language, text: String) {
        initialiseMary(language)

        val audio = mary.generateAudio(text)
        playAudio(audio)
    }

    /**
     * Play the given [audio]
     */
    private fun playAudio(audio: AudioInputStream) {
        try {
            audioPlayer.apply {
                setAudio(audio)
                start()
            }
        } catch (ex: IllegalStateException) {
            audioQueue.add(audio)
        }
    }
}