package mtg.game.objects

import mtg.cards.CardPrinting
import mtg.game.{GameStartingData, PlayerIdentifier, Zone}

import scala.util.Random

case class GameObjectState(
    nextObjectId: Int,
    libraries: Map[PlayerIdentifier, Seq[GameObject]],
    hands: Map[PlayerIdentifier, Seq[GameObject]],
    battlefield: Seq[GameObject],
    sideboards: Map[PlayerIdentifier, Seq[GameObject]])
{
  def updateZone(zone: Zone, objectsUpdater: Seq[GameObject] => Seq[GameObject]): GameObjectState = {
    zone.stateLens.modify(objectsUpdater)(this)
  }
  def createNewObjectForZone(oldObject: GameObject, newZone: Zone): (GameObject, GameObjectState) = {
    (oldObject.forNewZone(ObjectId(nextObjectId), newZone), copy(nextObjectId = nextObjectId + 1))
  }
  def allVisibleObjects(player: PlayerIdentifier): Seq[GameObject] = {
    hands(player) ++ battlefield
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
      playerIdentifier -> Random.shuffle(deck).map(createCardObject(_, playerIdentifier, Zone.Library(playerIdentifier)))
    }).toMap
    val hands = gameStartingData.playerData.map(playerStartingData => {
      import playerStartingData._
      playerIdentifier -> Nil
    }).toMap
    val sideboards = gameStartingData.playerData.map(playerStartingData => {
      import playerStartingData._
      playerIdentifier -> sideboard.map(createCardObject(_, playerIdentifier, Zone.Sideboard(playerIdentifier)))
    }).toMap

    GameObjectState(
      nextObjectId,
      libraries,
      hands,
      Nil,
      sideboards)
  }
}
