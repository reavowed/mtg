package mtg.game

import monocle.Focus
import mtg.game.`object`.{GameObject, ObjectId}
import mtg.game.zone.{Zone, ZoneState, ZoneStates}

case class GameState(
  nextObjectId: Int,
  playersInTurnOrder: Seq[PlayerIdentifier],
  zoneStates: ZoneStates,
  sideboards: Map[PlayerIdentifier, Sideboard])
{
  def updateZone(zone: Zone, zoneStateUpdater: ZoneState => ZoneState): GameState = {
    Focus[GameState](_.zoneStates).andThen(zone.stateLens).modify(zoneStateUpdater)(this)
  }
  def getPlayersInApNapOrder(activePlayer: PlayerIdentifier): Seq[PlayerIdentifier] = {
    GameState.getPlayersInApNapOrder(activePlayer, playersInTurnOrder)
  }
  def newObjectForZone(oldObject: GameObject, newZone: Zone): (GameObject, GameState) = {
    (oldObject.forNewZone(ObjectId(nextObjectId), newZone), copy(nextObjectId = nextObjectId + 1))
  }
}

object GameState {
  def getPlayersInApNapOrder(
    activePlayer: PlayerIdentifier,
    playersInTurnOrder: Seq[PlayerIdentifier]
  ): Seq[PlayerIdentifier] = {
    val playersBefore = playersInTurnOrder.takeWhile(_ != activePlayer)
    val playersOnOrAfter = playersInTurnOrder.dropWhile(_ != activePlayer)
    playersOnOrAfter ++ playersBefore
  }
}
