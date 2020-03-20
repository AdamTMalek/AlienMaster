package app.serialcom

/**
 * The [MessageFromYamlBuilder] is an abstract class
 * for any builder that takes the incoming data
 * and parses is into type [T] in the YAML format.
 */
abstract class MessageFromYamlBuilder<T> {
    /**
     * Automatically set the field based on the key from the line (a line has a key-value pair)
     */
    abstract fun set(line: String): MessageFromYamlBuilder<T>

    /**
     * Checks if the object is ready to be built (are necessary fields set)
     */
    abstract fun isReady(): Boolean

    /**
     * Builds the object based on the set fields
     */
    abstract fun build(): T

    /**
     * Filters out every line that is does not provide any
     * information to the message.
     */
    protected fun getMessageContent(data: String): String {
        return data.lines().filter { !it.contains("{") && !it.contains("}") }.joinToString("\n").trimIndent()
    }

    /**
     * Parse a line in a format of
     * key: value
     * as a pair of <key, value> (both [String]) and return it
     */
    protected fun parseLineAsPair(line: String): Pair<String, String> {
        // First group is a match of a word or bunch of words to a colon,
        // the second is any alphanumerical character, dot or a colon.
        val regex = Regex("(\\w+): ([-A-Za-z0-9:._, ]+)")
        val matchGroups = regex.find(line)!!.groups

        return Pair(matchGroups[1]!!.value, matchGroups[2]!!.value)
    }
}