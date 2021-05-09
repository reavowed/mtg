package mtg.game

import mtg.game.zone.ZoneStates

case class GameState(
  nextObjectId: Int,
  zoneStates: ZoneStates,
  sideboards: Map[PlayerIdentifier, Sideboard]
)
