package app.tts

import app.Language
import marytts.LocalMaryInterface
import marytts.util.data.audio.AudioPlayer
import java.util.*

/**
 * The [TextToSpeech] class acts as an interface to the actual library used for
 * doing text-to-speech using MaryTTS.
 *
 * Use the [say] method to use the text-to-speech.
 */
class TextToSpeech {
    companion object {
        const val GERMAN_VOICE = "bits1-hsmm"
        const val ENGLISH_VOICE = "dfki-poppy-hsmm"

        fun getAllAvailableLocales() = LocalMaryInterface().availableLocales

        fun getAllAvailableVoices() = LocalMaryInterface().availableVoices
    }

    private val mary = LocalMaryInterface()
    private val audioPlayer = AudioPlayer()

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
     * Say the given [text] in the given [language]
     */
    fun say(language: Language, text: String) {
        initialiseMary(language)

        val audio = mary.generateAudio(text)
        audioPlayer.apply {
            setAudio(audio)
            start()
        }
    }
}