package app

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

object PlayersDatabase {

    init {
        Database.connect("jdbc:sqlite:./resources/database.sqlite", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }

    fun createPlayers() {
        transaction {
            SchemaUtils.create(Players)

            Player.new {
                name = "Player 1"
                score = 20
            }
        }

    }

    fun getAllPlayers(): List<Player> {
        return transaction {
            Player.all().toList()
        }
    }
}