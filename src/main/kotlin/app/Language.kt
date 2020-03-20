package app

import java.util.*

/**
 * The [Language] enum class is a helper class for storing any
 * language-related information, for example [code], [locale] (used for TTS) and [readableName]
 * if we want to display it.
 */
enum class Language(val code: String, val locale: Locale, private val readableName: String) {
    ENG("eng", Locale.UK, "English"),
    GER("ger", Locale.GERMANY, "German");

    /**
     * Returns the [readableName]
     */
    override fun toString(): String {
        return readableName
    }

    companion object {
        /**
         * Translates the given [code] into equivalent enum value of the [Language] class
         */
        fun fromCode(code: String): Language {
            return values().find { it.code == code }
                ?: throw LanguageNotFoundException(code)
        }
    }
}

/**
 * Thrown when [Language.fromCode] fails to find equivalent enum value.
 */
class LanguageNotFoundException(code: String) : Exception("No language was found with the code $code")