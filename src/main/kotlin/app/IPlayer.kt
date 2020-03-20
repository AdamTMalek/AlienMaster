package app

/**
 * Any class that represents a player must implement
 * this interface.
 */
interface IPlayer {
    var name: String
    var language: String
    var score: Int

    fun getIdValue(): Int
}