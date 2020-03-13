package app

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

/**
 * The [PlayersDatabase] is an object managing all operations with the database, like getting all players,
 * adding, and modifying a player.
 */
object PlayersDatabase : PlayersDatabaseStorage {

    init {
        Database.connect("jdbc:sqlite:./resources/database.sqlite", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        createPlayersTable()
    }

    /**
     * Create the [Players] table in the database
     */
    override fun createPlayersTable() {
        transaction {
            SchemaUtils.create(Players)
        }
    }

    /**
     * Drop (delete) the [Players] table from the database
     */
    override fun dropPlayersTable() {
        transaction {
            SchemaUtils.drop(Players)
        }
    }

    /**
     * Add player with the following properties to the database
     */
    override fun addPlayer(name: String, language: String, score: Int) {
        transaction {
            Player.new {
                this.name = name
                this.language = language
                this.score = score
            }
        }
    }

    /**
     * Change name of the given [player] to [newName]
     */
    override fun changeName(player: IPlayer, newName: String) {
        transaction {
            player.name = newName
        }
    }

    /**
     * Change the preferred language of the given [player] to [newLanguage]
     *
     * @param newLanguage language code
     */
    override fun changeLanguage(player: IPlayer, newLanguage: String) {
        transaction {
            player.language = newLanguage
        }
    }

    /**
     * Change the top score of the given [player] to the [newScore]
     */
    override fun changeScore(player: IPlayer, newScore: Int) {
        transaction {
            player.score = newScore
        }
    }

    /**
     * Get all players stored in the database
     */
    override fun getAllPlayers(): List<IPlayer> {
        return transaction {
            Player.all().toList()
        }
    }

    /**
     * Get player by the given id
     */
    override fun getPlayerById(id: Int): IPlayer? {
        return transaction {
            Player.findById(id)
        }
    }
}