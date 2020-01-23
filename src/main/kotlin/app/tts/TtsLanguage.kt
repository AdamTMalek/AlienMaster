package app.tts

enum class TtsLanguage(val code: String, private val readableName: String) {
    ENG("eng", "English"),
    GER("ger", "German");

    override fun toString(): String {
        return readableName
    }
}