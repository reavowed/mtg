package mtg.game

case class GameData(playersInTurnOrder: Seq[PlayerIdentifier], startingHandSize: Int, startingLifeTotal: Int) {
  def getPlayersInApNapOrder(activePlayer: PlayerIdentifier): Seq[PlayerIdentifier] = {
    GameData.getPlayersInApNapOrder(activePlayer, playersInTurnOrder)
  }
  def getNextPlayerInTurnOrder(currentPlayer: PlayerIdentifier): PlayerIdentifier = {
    (playersInTurnOrder :+ playersInTurnOrder.head).dropWhile(_ != currentPlayer).tail.head
  }
}

object GameData {
  def initial(playersInTurnOrder: Seq[PlayerIdentifier]): GameData = GameData(playersInTurnOrder, 7, 20)

  def getPlayersInApNapOrder(
    activePlayer: PlayerIdentifier,
    playersInTurnOrder: Seq[PlayerIdentifier]
  ): Seq[PlayerIdentifier] = {
    val playersBefore = playersInTurnOrder.takeWhile(_ != activePlayer)
    val playersOnOrAfter = playersInTurnOrder.dropWhile(_ != activePlayer)
    playersOnOrAfter ++ playersBefore
  }
}
