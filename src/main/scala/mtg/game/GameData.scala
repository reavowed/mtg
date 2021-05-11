package mtg.game

case class GameData(playersInTurnOrder: Seq[PlayerIdentifier]) {
  def getPlayersInApNapOrder(activePlayer: PlayerIdentifier): Seq[PlayerIdentifier] = {
    GameData.getPlayersInApNapOrder(activePlayer, playersInTurnOrder)
  }
}

object GameData {
  def getPlayersInApNapOrder(
    activePlayer: PlayerIdentifier,
    playersInTurnOrder: Seq[PlayerIdentifier]
  ): Seq[PlayerIdentifier] = {
    val playersBefore = playersInTurnOrder.takeWhile(_ != activePlayer)
    val playersOnOrAfter = playersInTurnOrder.dropWhile(_ != activePlayer)
    playersOnOrAfter ++ playersBefore
  }
}
