package app

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Players : Table() {
    val id: Column<Int> = integer("id").autoIncrement().primaryKey()
    val name: Column<String> = varchar("name", 50)
    val score: Column<Int> = integer("score")

    override val primaryKey = PrimaryKey(id)
}
