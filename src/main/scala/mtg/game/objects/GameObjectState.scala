package mtg.game.objects

import mtg.cards.CardPrinting
import mtg.game.{GameStartingData, PlayerIdentifier}
import mtg.game.zone.{Zone, ZoneState}

import scala.util.Random

case class GameObjectState(
    nextObjectId: Int,
    libraries: Map[PlayerIdentifier, ZoneState],
    hands: Map[PlayerIdentifier, ZoneState],
    sideboards: Map[PlayerIdentifier, ZoneState])
{
  def updateZone(zone: Zone, zoneStateUpdater: ZoneState => ZoneState): GameObjectState = {
    zone.stateLens.modify(zoneStateUpdater)(this)
  }
  def createNewObjectForZone(oldObject: GameObject, newZone: Zone): (GameObject, GameObjectState) = {
    (oldObject.forNewZone(ObjectId(nextObjectId), newZone), copy(nextObjectId = nextObjectId + 1))
  }
}

object GameObjectState {
  def initial(gameStartingData: GameStartingData) = {
    var nextObjectId = 1
    def getNextObjectId = {
      val objectId = ObjectId(nextObjectId)
      nextObjectId += 1
      objectId
    }
    def createCardObject(cardPrinting: CardPrinting, playerIdentifier: PlayerIdentifier, zone: Zone): CardObject = {
      CardObject(Card(playerIdentifier, cardPrinting), getNextObjectId, zone)
    }
    val libraries = gameStartingData.playerData.map(playerStartingData => {
      import playerStartingData._
      playerIdentifier -> ZoneState(Random.shuffle(deck).map(createCardObject(_, playerIdentifier, Zone.Library(playerIdentifier))))
    }).toMap
    val hands = gameStartingData.playerData.map(playerStartingData => {
      import playerStartingData._
      playerIdentifier -> ZoneState(Nil)
    }).toMap
    val sideboards = gameStartingData.playerData.map(playerStartingData => {
      import playerStartingData._
      playerIdentifier -> ZoneState(sideboard.map(createCardObject(_, playerIdentifier, Zone.Sideboard(playerIdentifier))))
    }).toMap

    GameObjectState(
      nextObjectId,
      libraries,
      hands,
      sideboards)
  }
}
