package app

/**
 * This interface should be implemented by a class
 * handling the database interactions where
 * players are stored.
 */
interface PlayersDatabaseStorage {
    /**
     * Create the [Players] table in the database
     */
    fun createPlayersTable()

    /**
     * Drop (delete) the [Players] table from the database
     */
    fun dropPlayersTable()

    /**
     * Add player with the following properties to the database
     */
    fun addPlayer(name: String, language: String, score: Int)

    /**
     * Change name of the given [player] to [newName]
     */
    fun changeName(player: Player, newName: String)

    /**
     * Change the preferred language of the given [player] to [newLanguage]
     *
     * @param newLanguage language code
     */
    fun changeLanguage(player: Player, newLanguage: String)

    /**
     * Change the top score of the given [player] to the [newScore]
     */
    fun changeScore(player: Player, newScore: Int)

    /**
     * Get all players stored in the database
     */
    fun getAllPlayers(): List<Player>

    /**
     * Get player by the given id
     */
    fun getPlayerById(id: Int): Player?
}