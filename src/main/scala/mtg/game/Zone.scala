package mtg.game

import monocle.function.At
import monocle.{Focus, Lens}
import mtg.game.objects.{GameObject, GameObjectState}

sealed abstract class Zone {
  def stateLens: Lens[GameObjectState, Seq[GameObject]]
  def getState(gameObjectState: GameObjectState): Seq[GameObject] = stateLens.get(gameObjectState)
}

object Zone {
  sealed abstract class PlayerSpecific extends Zone {
    def playerIdentifier: PlayerIdentifier
    def stateMapLens: Lens[GameObjectState, Map[PlayerIdentifier, Seq[GameObject]]]
    override def stateLens: Lens[GameObjectState, Seq[GameObject]] = stateMapLens.at(playerIdentifier)(At(i => Lens((_: Map[PlayerIdentifier, Seq[GameObject]])(i))(v => map => (map - i) + (i -> v))))
  }
  sealed abstract class Shared extends Zone
  case class Library(playerIdentifier: PlayerIdentifier) extends PlayerSpecific {
    override def stateMapLens: Lens[GameObjectState, Map[PlayerIdentifier, Seq[GameObject]]] = Focus[GameObjectState](_.libraries)
  }
  case class Hand(playerIdentifier: PlayerIdentifier) extends PlayerSpecific {
    override def stateMapLens: Lens[GameObjectState, Map[PlayerIdentifier, Seq[GameObject]]] = Focus[GameObjectState](_.hands)
  }
  case class Sideboard(playerIdentifier: PlayerIdentifier) extends PlayerSpecific {
    override def stateMapLens: Lens[GameObjectState, Map[PlayerIdentifier, Seq[GameObject]]] = Focus[GameObjectState](_.sideboards)
  }
  case object Battlefield extends Shared {
    override def stateLens: Lens[GameObjectState, Seq[GameObject]] = Focus[GameObjectState](_.battlefield)
  }
}

