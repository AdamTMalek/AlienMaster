package app

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column


/**
 * This object defines the database table properties like the field it contains, what the primary key is etc.
 */
object Players : IntIdTable() {
    val name: Column<String> = varchar("name", 50)
    val language: Column<String> = varchar("language", 3) // ISO-639-2/B code
    val score: Column<Int> = integer("score")
    override val primaryKey = PrimaryKey(id)
}

/**
 * The [Player] class is used to represent a single entry from the [Players] table
 */
class Player(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Player>(Players)

    var name by Players.name
    var language by Players.language
    var score by Players.score
}
