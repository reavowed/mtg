package mtg.game

case class GameStartingData(playerData: Seq[PlayerStartingData]) {
  val players: Seq[PlayerIdentifier] = playerData.map(_.playerIdentifier)
}
