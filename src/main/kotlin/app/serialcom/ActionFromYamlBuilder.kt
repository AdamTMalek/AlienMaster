package app.serialcom

class ActionFromYamlBuilder {
    var action: String? = null
    var value: String? = null


    fun set(line: String) {
        val (key, value) = parseLineAsPair(line)

        when (key) {
            "action" -> action = value
            "value" -> this.value = value
        }
    }

    fun isReady(): Boolean {
        return action != null && value != null
    }

    @Throws(IllegalStateException::class)
    fun build(): Action {
        if (!isReady())
            throw IllegalStateException("The object is not ready to be built")

        return Action(action!!, value!!)
    }

    private fun parseLineAsPair(line: String): Pair<String, String> {
        val regex = Regex("(\\w+): ([A-Za-z0-9:.]+)")
        val matchGroups = regex.find(line)!!.groups

        return Pair(matchGroups[1]!!.value, matchGroups[2]!!.value)
    }
}