package app

import java.util.*

enum class Language(val code: String, val locale: Locale, private val readableName: String) {
    ENG("eng", Locale.UK, "English"),
    GER("ger", Locale.GERMANY, "German");

    override fun toString(): String {
        return readableName
    }

    companion object {
        fun fromCode(code: String): Language {
            return values().find { it.code == code }
                ?: throw LanguageNotFoundException(code)
        }
    }
}

class LanguageNotFoundException(code: String) : Exception("No language was found with the code $code")