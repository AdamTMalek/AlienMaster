package app.serialcom

/**
 * The [ActionFromYamlBuilder] takes a line coming from the serial port and builds an [Action] object by parsing
 * the line.
 * It exposes three public methods:
 * - [set] that automatically sets the right fields of the [Action] object
 * - [isReady] that indicates if the [Action] object is ready to be built or not
 * - [build] that builds [Action] and returns it
 */
class ActionFromYamlBuilder {
    var action: String? = null
    var value: String? = null

    /**
     * Given the [line] set the correct field of the [Action] object
     */
    fun set(line: String): ActionFromYamlBuilder {
        val (key, value) = parseLineAsPair(line)

        when (key) {
            "action" -> action = value
            "value" -> this.value = value
        }

        return this
    }

    /**
     * Returns true if the object is ready to be built
     */
    fun isReady(): Boolean {
        return action != null && value != null
    }

    /**
     * Returns the [Action] object with the set fields
     * @throws IllegalStateException when some fields were not set
     */
    @Throws(IllegalStateException::class)
    fun build(): Action {
        if (!isReady())
            throw IllegalStateException("The object is not ready to be built")

        return Action(action!!, value!!)
    }

    private fun parseLineAsPair(line: String): Pair<String, String> {
        // First group is a match of a word or bunch of words to a colon,
        // the second is any alphanumerical character, dot or a colon.
        val regex = Regex("(\\w+): ([A-Za-z0-9:.]+)")
        val matchGroups = regex.find(line)!!.groups

        return Pair(matchGroups[1]!!.value, matchGroups[2]!!.value)
    }
}