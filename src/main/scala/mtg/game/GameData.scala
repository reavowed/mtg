package mtg.game

import mtg.core.PlayerId

case class GameData(playersInTurnOrder: Seq[PlayerId], startingHandSize: Int, startingLifeTotal: Int) {
  def getPlayersInApNapOrder(activePlayer: PlayerId): Seq[PlayerId] = {
    GameData.getPlayersInApNapOrder(activePlayer, playersInTurnOrder)
  }
  def getNextPlayerInTurnOrder(currentPlayer: PlayerId): PlayerId = {
    (playersInTurnOrder :+ playersInTurnOrder.head).dropWhile(_ != currentPlayer).tail.head
  }
}

object GameData {
  def initial(playersInTurnOrder: Seq[PlayerId]): GameData = GameData(playersInTurnOrder, 7, 20)

  def getPlayersInApNapOrder(
    activePlayer: PlayerId,
    playersInTurnOrder: Seq[PlayerId]
  ): Seq[PlayerId] = {
    val playersBefore = playersInTurnOrder.takeWhile(_ != activePlayer)
    val playersOnOrAfter = playersInTurnOrder.dropWhile(_ != activePlayer)
    playersOnOrAfter ++ playersBefore
  }
}
