package app

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object Players : IntIdTable() {
    val name: Column<String> = varchar("name", 50)
    val score: Column<Int> = integer("score")

    override val primaryKey = PrimaryKey(id)
}

class Player(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Player>(Players)

    var name by Players.name
    var score by Players.score
}
