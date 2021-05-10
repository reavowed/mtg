package mtg.game.zone

import monocle.function.At
import monocle.{Focus, Lens}
import mtg.game.{GameState, PlayerIdentifier}

sealed abstract class Zone {
  def stateLens: Lens[ZoneStates, ZoneState]
  def getState(gameState: GameState): ZoneState = getState(gameState.zoneStates)
  def getState(zoneStates: ZoneStates): ZoneState = stateLens.get(zoneStates)
}

sealed abstract class PlayerSpecificZone extends Zone {
  def playerIdentifier: PlayerIdentifier
  def stateMapLens: Lens[ZoneStates, Map[PlayerIdentifier, ZoneState]]
  override def stateLens: Lens[ZoneStates, ZoneState] = stateMapLens.at(playerIdentifier)(At(i => Lens((_: Map[PlayerIdentifier, ZoneState])(i))(v => map => (map - i) + (i -> v))))
}

case class Library(playerIdentifier: PlayerIdentifier) extends PlayerSpecificZone {
  override def stateMapLens: Lens[ZoneStates, Map[PlayerIdentifier, ZoneState]] = Focus[ZoneStates](_.libraries)
}
case class Hand(playerIdentifier: PlayerIdentifier) extends PlayerSpecificZone {
  override def stateMapLens: Lens[ZoneStates, Map[PlayerIdentifier, ZoneState]] = Focus[ZoneStates](_.hands)
}
