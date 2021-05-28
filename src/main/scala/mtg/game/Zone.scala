package mtg.game

import monocle.function.At
import monocle.{Focus, Lens}
import mtg.game.objects.{GameObject, GameObjectState}
import mtg.game.state.{GameState, PermanentStatus}
import mtg.utils.AtGuaranteed

sealed abstract class ZoneType
object ZoneType {
  case object Library extends ZoneType
  case object Hand extends ZoneType
  case object Battlefield extends ZoneType
  case object Graveyard extends ZoneType
  case object Stack extends ZoneType
  case object Sideboard extends ZoneType
}

sealed abstract class Zone(val zoneType: ZoneType) {
  def stateLens: Lens[GameObjectState, Seq[GameObject]]
  def getState(gameState: GameState): Seq[GameObject] = getState(gameState.gameObjectState)
  def getState(gameObjectState: GameObjectState): Seq[GameObject] = stateLens.get(gameObjectState)
  def defaultPermanentStatus: Option[PermanentStatus] = None
}

object Zone {
  sealed abstract class PlayerSpecific(zoneType: ZoneType) extends Zone(zoneType) {
    def playerIdentifier: PlayerIdentifier
    def stateMapLens: Lens[GameObjectState, Map[PlayerIdentifier, Seq[GameObject]]]
    override def stateLens: Lens[GameObjectState, Seq[GameObject]] = stateMapLens.at(playerIdentifier)(AtGuaranteed.apply)
  }
  sealed abstract class Shared(zoneType: ZoneType) extends Zone(zoneType)

  case class Library(playerIdentifier: PlayerIdentifier) extends PlayerSpecific(ZoneType.Library) {
    override def stateMapLens: Lens[GameObjectState, Map[PlayerIdentifier, Seq[GameObject]]] = Focus[GameObjectState](_.libraries)
  }
  case class Hand(playerIdentifier: PlayerIdentifier) extends PlayerSpecific(ZoneType.Hand) {
    override def stateMapLens: Lens[GameObjectState, Map[PlayerIdentifier, Seq[GameObject]]] = Focus[GameObjectState](_.hands)
  }
  case object Battlefield extends Shared(ZoneType.Battlefield) {
    override def stateLens: Lens[GameObjectState, Seq[GameObject]] = Focus[GameObjectState](_.battlefield)
    override def defaultPermanentStatus: Option[PermanentStatus] = Some(PermanentStatus(false, false, false, false))
  }
  case class Graveyard(playerIdentifier: PlayerIdentifier) extends PlayerSpecific(ZoneType.Graveyard) {
    override def stateMapLens: Lens[GameObjectState, Map[PlayerIdentifier, Seq[GameObject]]] = Focus[GameObjectState](_.graveyards)
  }
  case object Stack extends Shared(ZoneType.Stack) {
    override def stateLens: Lens[GameObjectState, Seq[GameObject]] = Focus[GameObjectState](_.stack)
  }
  case class Sideboard(playerIdentifier: PlayerIdentifier) extends PlayerSpecific(ZoneType.Hand) {
    override def stateMapLens: Lens[GameObjectState, Map[PlayerIdentifier, Seq[GameObject]]] = Focus[GameObjectState](_.sideboards)
  }
}

