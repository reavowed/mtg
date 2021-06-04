package mtg.game.objects

import monocle.Focus
import mtg.cards.CardPrinting
import mtg.effects.ContinuousEffect
import mtg.effects.condition.Condition
import mtg.game.state.{DerivedState, ObjectWithState}
import mtg.game.{GameData, GameStartingData, ObjectId, PlayerId, TypedZone, Zone}
import mtg.utils.AtGuaranteed

import scala.collection.View
import scala.util.Random

case class GameObjectState(
    nextObjectId: Int,
    lifeTotals: Map[PlayerId, Int],
    libraries: Map[PlayerId, Seq[BasicGameObject]],
    hands: Map[PlayerId, Seq[BasicGameObject]],
    battlefield: Seq[PermanentObject],
    graveyards: Map[PlayerId, Seq[BasicGameObject]],
    stack: Seq[StackObject],
    exile: Seq[BasicGameObject],
    sideboards: Map[PlayerId, Seq[BasicGameObject]],
    manaPools: Map[PlayerId, Seq[ManaObject]],
    lastKnownInformation: Map[ObjectId, ObjectWithState],
    floatingActiveContinuousEffects: Seq[FloatingActiveContinuousEffect])
{
  lazy val derivedState: DerivedState = DerivedState.calculateFromGameObjectState(this)
  def activeContinuousEffects: View[ContinuousEffect] = floatingActiveContinuousEffects.view.map(_.effect) ++ DerivedState.getActiveContinuousEffectsFromStaticAbilities(derivedState.allObjectStates.values.view)

  def updateManaPool(player: PlayerId, poolUpdater: Seq[ManaObject] => Seq[ManaObject]): GameObjectState = {
    Focus[GameObjectState](_.manaPools).at(player)(AtGuaranteed.apply).modify(poolUpdater)(this)
  }
  def deleteObject(gameObject: GameObject): GameObjectState = {
    gameObject.removeFromCurrentZone(this)
      .copy(lastKnownInformation = derivedState.allObjectStates.get(gameObject.objectId).foldLeft(lastKnownInformation)(_.updated(gameObject.objectId, _)))
  }
  def addNewObject(createNewObject: ObjectId => GameObject, getIndex: Seq[GameObject] => Int): GameObjectState = {
    createNewObject(ObjectId(nextObjectId)).add(this, getIndex).copy(nextObjectId = nextObjectId + 1)
  }

  def allObjects: View[GameObject] = {
    libraries.flatMap(_._2).view ++
      hands.flatMap(_._2).view ++
      battlefield.view ++
      stack.view ++
      graveyards.flatMap(_._2).view ++
      exile.view
  }
  def updatePermanentObject(objectId: ObjectId, f: PermanentObject => PermanentObject): GameObjectState = {
    battlefield.find(_.objectId == objectId).get.update(this, f)
  }
  def updateStackObject(objectId: ObjectId, f: StackObject => StackObject): GameObjectState = {
    stack.find(_.objectId == objectId).get.update(this, f)
  }
  def updateLifeTotal(player: PlayerId, f: Int => Int): GameObjectState = {
    Focus[GameObjectState](_.lifeTotals).at(player)(AtGuaranteed.apply).modify(f)(this)
  }

  def addEffects(continuousEffects: Seq[ContinuousEffect], endCondition: Condition): GameObjectState = {
    updateEffects(_ ++ continuousEffects.map(FloatingActiveContinuousEffect(_, endCondition)))
  }
  def updateEffects(f: Seq[FloatingActiveContinuousEffect] => Seq[FloatingActiveContinuousEffect]): GameObjectState = {
    copy(floatingActiveContinuousEffects = f(floatingActiveContinuousEffects))
  }

  def getCurrentOrLastKnownState(objectId: ObjectId): Option[ObjectWithState] = {
    derivedState.allObjectStates.get(objectId) orElse lastKnownInformation.get(objectId)
  }
}

object GameObjectState {
  def initial(gameStartingData: GameStartingData, gameData: GameData) = {
    var nextObjectId = 1
    def getNextObjectId = {
      val objectId = ObjectId(nextObjectId)
      nextObjectId += 1
      objectId
    }
    def createGameObject(cardPrinting: CardPrinting, playerIdentifier: PlayerId, zone: TypedZone[BasicGameObject]): BasicGameObject = {
      BasicGameObject(Card(playerIdentifier, cardPrinting), getNextObjectId, zone)
    }
    def emptyMap[T]: Map[PlayerId, Seq[T]] = gameStartingData.playerData.map(_.playerIdentifier -> Nil).toMap
    val lifeTotals = gameStartingData.playerData.map(playerStartingData => {
      import playerStartingData._
      playerIdentifier -> gameData.startingLifeTotal
    }).toMap
    val libraries = gameStartingData.playerData.map(playerStartingData => {
      import playerStartingData._
      playerIdentifier -> Random.shuffle(deck).map(createGameObject(_, playerIdentifier, Zone.Library(playerIdentifier)))
    }).toMap
    val sideboards = gameStartingData.playerData.map(playerStartingData => {
      import playerStartingData._
      playerIdentifier -> sideboard.map(createGameObject(_, playerIdentifier, Zone.Sideboard(playerIdentifier)))
    }).toMap

    GameObjectState(
      nextObjectId,
      lifeTotals,
      libraries,
      emptyMap,
      Nil,
      emptyMap,
      Nil,
      Nil,
      sideboards,
      emptyMap,
      Map.empty,
      Nil)
  }
}
