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
object PlayersDatabase {

    init {
        Database.connect("jdbc:sqlite:./resources/database.sqlite", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        createPlayersTable()
    }

    /**
     * Create the [Players] table in the database
     */
    fun createPlayersTable() {
        transaction {
            SchemaUtils.create(Players)
        }
    }

    /**
     * Drop (delete) the [Players] table from the database
     */
    fun dropPlayersTable() {
        transaction {
            SchemaUtils.drop(Players)
        }
    }

    fun addPlayer(name: String, language: String, score: Int) {
        transaction {
            Player.new {
                this.name = name
                this.language = language
                this.score = score
            }
        }
    }

    fun changeName(player: Player, newName: String) {
        transaction {
            player.name = newName
        }
    }

    fun changeLanguage(player: Player, newLanguage: String) {
        transaction {
            player.language = newLanguage
        }
    }

    fun changeScore(player: Player, newScore: Int) {
        transaction {
            player.score = newScore
        }
    }

    fun getAllPlayers(): List<Player> {
        return transaction {
            Player.all().toList()
        }
    }

    fun getPlayerById(id: Int): Player? {
        return transaction {
            Player.findById(id)
        }
    }
}