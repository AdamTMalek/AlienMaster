package app

class TestPlayer(
    private val id: Int,
    override var name: String,
    override var language: String,
    override var score: Int
) : IPlayer {
    override fun getIdValue(): Int {
        return id
    }
}

class DatabaseMock : PlayersDatabaseStorage {
    private val players = mutableListOf<IPlayer>()
    override fun createPlayersTable() {
    }

    override fun dropPlayersTable() {
    }

    override fun addPlayer(name: String, language: String, score: Int) {
        players.add(TestPlayer(players.size + 1, name, language, score))
    }

    override fun changeName(player: IPlayer, newName: String) {
        player.name = newName
    }

    override fun changeLanguage(player: IPlayer, newLanguage: String) {
        player.language = newLanguage
    }

    override fun changeScore(player: IPlayer, newScore: Int) {
        player.score = newScore
    }

    override fun getAllPlayers(): List<IPlayer> {
        return players
    }

    override fun getPlayerById(id: Int): IPlayer? {
        return players.find { it.getIdValue() == id }
    }
}