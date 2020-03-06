package app

/**
 * Raised when [PlayersDatabase] returned null from method [PlayersDatabase.getPlayerById]
 * with the given id
 */
class PlayerNotFoundException(id: Int) : Exception("Player with id $id does not exist in the database")