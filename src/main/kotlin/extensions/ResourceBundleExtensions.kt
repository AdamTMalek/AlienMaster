package extensions

import java.text.MessageFormat
import java.util.*

/**
 * This method will take the string using [ResourceBundle.getString] method on the
 * bundle object that is expected to be parameterized with curly brackets, for example
 * "Hello {0} world {1}"
 * The parameters placeholders will be then substituted with actual parameters from the
 * passed collection of [params]
 */
fun ResourceBundle.getParameterizedString(key: String, vararg params: Any): String {
    /**
     * Checks if the string contains parameter placeholders, e.g. {0}
     */
    fun stringIsParameterized(string: String): Boolean {
        val regex = Regex("\\{[\\d]+}")
        return regex.find(string) != null
    }

    val text = this.getString(key)

    return if (params.isNotEmpty() && stringIsParameterized(text)) {
        val format = MessageFormat(text, this.locale)
        format.format(params, StringBuffer(), null).toString()
    } else {
        text
    }
}