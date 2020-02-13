package app

import java.io.File
import kotlin.random.Random

/**
 * The [PlayerGenerator] class can be used to generate random players
 * both English and German speaking, that can then be put inside the
 * database.
 */
class PlayerGenerator {
    class Player(val name: String, val language: Language, var score: Int)

    private val players: Set<Player> // All English and German players with 0 score

    init {
        val englishNames = readNamesFromFile("english_names.txt")
        val germanNames = readNamesFromFile("german_names.txt")

        val englishPlayers = generatePlayersFromNames(englishNames, Language.ENG)
        val germanPlayers = generatePlayersFromNames(germanNames, Language.GER)

        players = englishPlayers.union(germanPlayers)
    }

    /**
     * Get a list of names from the given file.
     * @param filename is only the filename (with extension). The directory will be added by the method.
     */
    private fun readNamesFromFile(filename: String): List<String> {
        val url = this::class.java.classLoader.getResource("player_generator/$filename")!!.toURI()
        val file = File(url)
        return file.readLines()
    }

    /**
     * Generate list of [Player] objects from the given list of names.
     * @param language Defines language of the players
     */
    private fun generatePlayersFromNames(names: List<String>, language: Language): List<Player> {
        return names.map { name -> Player(name, language, 0) }
    }

    /**
     * Generate random players with names taken from the player_generator resources.
     * @param numberOfPlayers Number of players to generate
     * @param randomScore If set to true, players will have a random score between 0 and 30
     */
    fun generatePlayers(numberOfPlayers: Int, randomScore: Boolean): List<Player> {
        if (numberOfPlayers < 1)
            throw IllegalArgumentException("The number of players to generate cannot be less than 1")

        val generatedPlayers = mutableListOf<Player>()

        for (i in 1..numberOfPlayers) {
            val player = this.players.random()

            if (randomScore) {
                player.score = Random.nextInt(0, 30)
            }

            generatedPlayers.add(player)
        }

        return generatedPlayers
    }
}
