package mtg.game.zone

import monocle.function.At
import monocle.{Focus, Lens}
import mtg.game.objects.GameObjectState
import mtg.game.PlayerIdentifier

sealed abstract class Zone {
  def stateLens: Lens[GameObjectState, ZoneState]
  def getState(gameObjectState: GameObjectState): ZoneState = stateLens.get(gameObjectState)
}

object Zone {
  sealed abstract class PlayerSpecific extends Zone {
    def playerIdentifier: PlayerIdentifier
    def stateMapLens: Lens[GameObjectState, Map[PlayerIdentifier, ZoneState]]
    override def stateLens: Lens[GameObjectState, ZoneState] = stateMapLens.at(playerIdentifier)(At(i => Lens((_: Map[PlayerIdentifier, ZoneState])(i))(v => map => (map - i) + (i -> v))))
  }
  case class Library(playerIdentifier: PlayerIdentifier) extends PlayerSpecific {
    override def stateMapLens: Lens[GameObjectState, Map[PlayerIdentifier, ZoneState]] = Focus[GameObjectState](_.libraries)
  }
  case class Hand(playerIdentifier: PlayerIdentifier) extends PlayerSpecific {
    override def stateMapLens: Lens[GameObjectState, Map[PlayerIdentifier, ZoneState]] = Focus[GameObjectState](_.hands)
  }
  case class Sideboard(playerIdentifier: PlayerIdentifier) extends PlayerSpecific {
    override def stateMapLens: Lens[GameObjectState, Map[PlayerIdentifier, ZoneState]] = Focus[GameObjectState](_.sideboards)
  }
}

