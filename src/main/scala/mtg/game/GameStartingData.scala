package mtg.game

import mtg.definitions.PlayerId

case class GameStartingData(playerData: Seq[PlayerStartingData]) {
  val players: Seq[PlayerId] = playerData.map(_.playerIdentifier)
}
