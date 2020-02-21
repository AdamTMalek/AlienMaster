package app.serialcom

/**
 * A value from [StateMessage] class determines the state
 * that the MBED is currently entering.
 */
enum class State(val code: String) {
    WAITING("WAITING"),
    PLAYER_DETECTED("DETECTED"),
    CARD_INSERTED("CARD_IN"),
    PLAYING("PLAYING"),
    GAME_OVER("OVER");

    companion object {
        /**
         * Parses the value of state (i.e. the string after the colon)
         * to the equivalent enum value.
         * @throws UnknownStateCodeException when no equivalent value was found
         */
        @Throws(UnknownStateCodeException::class)
        fun fromString(string: String): State {
            return values().find { it.code == string.toUpperCase() }
                ?: throw UnknownStateCodeException(string)
        }
    }
}

/**
 * Raised when no enum value was found when parsing it from string (using [State.fromString])
 */
class UnknownStateCodeException(code: String) : Exception("Unknown state code $code")