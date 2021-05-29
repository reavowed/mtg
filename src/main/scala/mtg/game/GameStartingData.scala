package mtg.game

case class GameStartingData(playerData: Seq[PlayerStartingData]) {
  val players: Seq[PlayerId] = playerData.map(_.playerIdentifier)
}
