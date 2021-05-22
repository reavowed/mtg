package mtg.game.objects

import monocle.Focus
import mtg.cards.CardPrinting
import mtg.game.{GameStartingData, PlayerIdentifier, Zone}
import mtg.utils.AtGuaranteed

import scala.util.Random

case class GameObjectState(
    nextObjectId: Int,
    libraries: Map[PlayerIdentifier, Seq[GameObject]],
    hands: Map[PlayerIdentifier, Seq[GameObject]],
    battlefield: Seq[GameObject],
    sideboards: Map[PlayerIdentifier, Seq[GameObject]],
    manaPools: Map[PlayerIdentifier, Seq[ManaObject]])
{
  def updateZone(zone: Zone, objectsUpdater: Seq[GameObject] => Seq[GameObject]): GameObjectState = {
    zone.stateLens.modify(objectsUpdater)(this)
  }
  def updateManaPool(player: PlayerIdentifier, poolUpdater: Seq[ManaObject] => Seq[ManaObject]): GameObjectState = {
    Focus[GameObjectState](_.manaPools).at(player)(AtGuaranteed.apply).modify(poolUpdater)(this)
  }
  def createNewObjectForZone(oldObject: GameObject, newZone: Zone): (GameObject, GameObjectState) = {
    (oldObject.setObjectId(ObjectId(nextObjectId)).setZone(newZone).setPermanentStatus(newZone.defaultPermanentStatus), copy(nextObjectId = nextObjectId + 1))
  }
  def allObjects: Seq[GameObject] = {
    (libraries.flatMap(_._2) ++ hands.flatMap(_._2) ++ battlefield).toSeq
  }
  def allVisibleObjects(player: PlayerIdentifier): Seq[GameObject] = {
    hands(player) ++ battlefield
  }
  def updateGameObject(oldGameObject: GameObject, newGameObject: GameObject): GameObjectState = {
    oldGameObject.zone.stateLens.modify(_.map(o => if (o == oldGameObject) newGameObject else o))(this)
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
      CardObject(Card(playerIdentifier, cardPrinting), getNextObjectId, zone, None, None)
    }
    def emptyMap[T]: Map[PlayerIdentifier, Seq[T]] = gameStartingData.playerData.map(_.playerIdentifier -> Nil).toMap
    val libraries = gameStartingData.playerData.map(playerStartingData => {
      import playerStartingData._
      playerIdentifier -> Random.shuffle(deck).map(createCardObject(_, playerIdentifier, Zone.Library(playerIdentifier)))
    }).toMap
    val sideboards = gameStartingData.playerData.map(playerStartingData => {
      import playerStartingData._
      playerIdentifier -> sideboard.map(createCardObject(_, playerIdentifier, Zone.Sideboard(playerIdentifier)))
    }).toMap

    GameObjectState(
      nextObjectId,
      libraries,
      emptyMap,
      Nil,
      sideboards,
      emptyMap)
  }
}
