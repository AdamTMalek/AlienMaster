package app.serialcom

abstract class MessageFromYamlBuilder<T> {
    abstract fun set(line: String): MessageFromYamlBuilder<T>
    abstract fun isReady(): Boolean
    abstract fun build(): T

    /**
     * Parse a line in a format of
     * key: value
     * as a pair of <key, value> (both [String]) and return it
     */
    protected fun parseLineAsPair(line: String): Pair<String, String> {
        // First group is a match of a word or bunch of words to a colon,
        // the second is any alphanumerical character, dot or a colon.
        val regex = Regex("(\\w+): ([-A-Za-z0-9:.]+)")
        val matchGroups = regex.find(line)!!.groups

        return Pair(matchGroups[1]!!.value, matchGroups[2]!!.value)
    }
}