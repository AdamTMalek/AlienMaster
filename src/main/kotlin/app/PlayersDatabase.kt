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
        createTable()
    }

    fun createTable() {
        transaction {
            SchemaUtils.create(Players)
        }
    }

    fun dropTable() {
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


    fun getAllPlayers(): List<Player> {
        return transaction {
            Player.all().toList()
        }
    }
}