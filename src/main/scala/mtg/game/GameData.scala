package mtg.game

case class GameData(playersInTurnOrder: Seq[PlayerIdentifier], startingHandSize: Int) {
  def getPlayersInApNapOrder(activePlayer: PlayerIdentifier): Seq[PlayerIdentifier] = {
    GameData.getPlayersInApNapOrder(activePlayer, playersInTurnOrder)
  }
}

object GameData {
  def initial(playersInTurnOrder: Seq[PlayerIdentifier]): GameData = GameData(playersInTurnOrder, 7)

  def getPlayersInApNapOrder(
    activePlayer: PlayerIdentifier,
    playersInTurnOrder: Seq[PlayerIdentifier]
  ): Seq[PlayerIdentifier] = {
    val playersBefore = playersInTurnOrder.takeWhile(_ != activePlayer)
    val playersOnOrAfter = playersInTurnOrder.dropWhile(_ != activePlayer)
    playersOnOrAfter ++ playersBefore
  }
}
