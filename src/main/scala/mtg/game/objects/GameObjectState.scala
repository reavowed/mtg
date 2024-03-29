package mtg.game.objects

import monocle.{Focus, Lens}
import mtg.abilities.{PendingTriggeredAbility, TriggeredAbility}
import mtg.cards.CardPrinting
import mtg.continuousEffects.{ContinuousEffect, FloatingActiveContinuousEffect}
import mtg.definitions.zones.Zone
import mtg.definitions.zones.Zone.BasicZone
import mtg.definitions.{ManaType, ObjectId, PlayerId}
import mtg.effects.InstructionResolutionContext
import mtg.effects.condition.Condition
import mtg.game._
import mtg.game.state.{DerivedState, ObjectWithState}
import mtg.utils.AtGuaranteed

import scala.collection.View
import scala.util.Random

case class GameObjectState(
    nextId: Int,
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
    floatingActiveContinuousEffects: Seq[FloatingActiveContinuousEffect],
    triggeredAbilitiesWaitingToBePutOnStack: Seq[PendingTriggeredAbility])
{
  lazy val derivedState: DerivedState = DerivedState.calculateFromGameObjectState(this)
  def activeContinuousEffects: View[ContinuousEffect] = {
    ContinuousEffect.fromRules.view ++
      floatingActiveContinuousEffects.view.map(_.effect) ++
      DerivedState.getActiveContinuousEffectsFromStaticAbilities(derivedState.allObjectStates.values.view)
  }
  def activeTriggeredAbilities: View[TriggeredAbility] = DerivedState.getActiveTriggeredAbilities(derivedState.allObjectStates.values.view)

  def addMana(playerId: PlayerId, manaType: ManaType): GameObjectState = {
    updateManaPool(playerId, _ :+ ManaObject(nextId, manaType)).copy(nextId = nextId + 1)
  }
  def updateManaPool(player: PlayerId, poolUpdater: Seq[ManaObject] => Seq[ManaObject]): GameObjectState = {
    Focus[GameObjectState](_.manaPools).at(player)(AtGuaranteed.apply).modify(poolUpdater)(this)
  }

  private def libraryLens(player: PlayerId): Lens[GameObjectState, Seq[BasicGameObject]] = Focus[GameObjectState](_.libraries).at(player)(AtGuaranteed.apply)
  private def handLens(player: PlayerId): Lens[GameObjectState, Seq[BasicGameObject]] = Focus[GameObjectState](_.hands).at(player)(AtGuaranteed.apply)
  private def graveyardLens(player: PlayerId): Lens[GameObjectState, Seq[BasicGameObject]] = Focus[GameObjectState](_.graveyards).at(player)(AtGuaranteed.apply)
  private def sideboardLens(player: PlayerId): Lens[GameObjectState, Seq[BasicGameObject]] = Focus[GameObjectState](_.sideboards).at(player)(AtGuaranteed.apply)
  private val battlefieldLens: Lens[GameObjectState, Seq[PermanentObject]] = Focus[GameObjectState](_.battlefield)
  private val stackLens: Lens[GameObjectState, Seq[StackObject]] = Focus[GameObjectState](_.stack)
  private val exileLens: Lens[GameObjectState, Seq[BasicGameObject]] = Focus[GameObjectState](_.exile)

  def addObjectToLibrary(player: PlayerId, objectConstructor: ObjectId => BasicGameObject, getIndex: Seq[BasicGameObject] => Int): (ObjectId, GameObjectState) = {
    addObjectToZone[BasicGameObject](libraryLens(player), objectConstructor, getIndex)
  }
  def addObjectToHand(player: PlayerId, objectConstructor: ObjectId => BasicGameObject): (ObjectId, GameObjectState) = {
    addObjectToZone[BasicGameObject](handLens(player), objectConstructor, _.length)
  }
  def addObjectToGraveyard(player: PlayerId, objectConstructor: ObjectId => BasicGameObject): (ObjectId, GameObjectState) = {
    addObjectToZone[BasicGameObject](graveyardLens(player), objectConstructor, _.length)
  }
  def addObjectToBattlefield(objectConstructor: ObjectId => PermanentObject): (ObjectId, GameObjectState) = {
    addObjectToZone[PermanentObject](battlefieldLens, objectConstructor, _.length)
  }
  def addObjectToStack(objectConstructor: ObjectId => StackObject): (ObjectId, GameObjectState) = {
    addObjectToZone[StackObject](stackLens, objectConstructor, _.length)
  }
  def addObjectToExile(objectConstructor: ObjectId => BasicGameObject): (ObjectId, GameObjectState) = {
    addObjectToZone[BasicGameObject](exileLens, objectConstructor, _.length)
  }
  def addObjectToSideboard(player: PlayerId, objectConstructor: ObjectId => BasicGameObject): (ObjectId, GameObjectState) = {
    addObjectToZone[BasicGameObject](sideboardLens(player), objectConstructor, _.length)
  }
  private def addObjectToZone[T <: GameObject](lens: Lens[GameObjectState, Seq[T]], objectConstructor: ObjectId => T, getIndex: Seq[T] => Int): (ObjectId, GameObjectState) = {
    val newObjectId = ObjectId(nextId)
    val newObject = objectConstructor(newObjectId)
    val newGameObjectState = lens.modify(contents => contents.insertAtIndex(newObject, getIndex(contents)))(this)
      .copy(nextId = nextId + 1)
    (newObjectId, newGameObjectState)
  }

  def updateObject[T1 <: GameObject](oldObject: T1, f: T1 => T1): GameObjectState = {
    val newObject = f(oldObject)
    updateZone(oldObject.zone, new ZoneUpdater {
      override def apply[T2 <: GameObject](seq: Seq[T2]): Seq[T2] = seq.map(o => if (o == oldObject) newObject.asInstanceOf[T2] else o)
    })
  }
  def updateObject[T1 <: GameObject](oldObject: Option[T1], f: T1 => T1): GameObjectState = {
    oldObject.map(updateObject(_, f)).getOrElse(this)
  }
  def deleteObject(gameObject: GameObject): GameObjectState = {
    updateZone(gameObject.zone, ObjectDeleter(gameObject))
      .copy(lastKnownInformation = derivedState.allObjectStates.get(gameObject.objectId).foldLeft(lastKnownInformation)(_.updated(gameObject.objectId, _)))
  }

  trait ZoneUpdater {
    def apply[T <: GameObject](seq: Seq[T]): Seq[T]
  }
  case class ObjectDeleter(gameObject: GameObject) extends ZoneUpdater {
    override def apply[T <: GameObject](seq: Seq[T]): Seq[T] = {
      seq.filter(_ != gameObject)
    }
  }

  private def updateZone(zone: Zone, zoneUpdater: ZoneUpdater): GameObjectState = {
    zone match {
      case Zone.Library(player) => copy(libraries = libraries.updated(player, zoneUpdater(libraries(player))))
      case Zone.Hand(player) => copy(hands = hands.updated(player, zoneUpdater(hands(player))))
      case Zone.Graveyard(player) => copy(graveyards = graveyards.updated(player, zoneUpdater(graveyards(player))))
      case Zone.Battlefield => copy(battlefield = zoneUpdater(battlefield))
      case Zone.Stack => copy(stack = zoneUpdater(stack))
      case Zone.Exile => copy(exile = zoneUpdater(exile))
      case Zone.Sideboard(player) => copy(sideboards = sideboards.updated(player, zoneUpdater(sideboards(player))))
    }
  }

  private def getZoneLens(zone: BasicZone): Lens[GameObjectState, Seq[BasicGameObject]] = zone match {
      case Zone.Library(player) => Focus[GameObjectState](_.libraries).at(player)(AtGuaranteed.apply)
      case Zone.Hand(player) => Focus[GameObjectState](_.hands).at(player)(AtGuaranteed.apply)
      case Zone.Graveyard(player) => Focus[GameObjectState](_.graveyards).at(player)(AtGuaranteed.apply)
      case Zone.Exile => Focus[GameObjectState](_.exile)
      case Zone.Sideboard(player) => Focus[GameObjectState](_.sideboards).at(player)(AtGuaranteed.apply)
  }
  def updateZone(zone: BasicZone, f: Seq[BasicGameObject] => Seq[BasicGameObject]): GameObjectState = {
    getZoneLens(zone).modify(f)(this)
  }

  def allObjects: View[GameObject] = {
    libraries.flatMap(_._2).view ++
      hands.flatMap(_._2).view ++
      battlefield.view ++
      stack.view ++
      graveyards.flatMap(_._2).view ++
      exile.view ++
      sideboards.flatMap(_._2).view
  }
  def updateObjectById(objectId: ObjectId, f: GameObject => GameObject): GameObjectState = {
    updateObject(allObjects.find(_.objectId == objectId), f)
  }
  def updatePermanentObject(objectId: ObjectId, f: PermanentObject => PermanentObject): GameObjectState = {
    updateObject(battlefield.find(_.objectId == objectId), f)
  }
  def updateStackObject(objectId: ObjectId, f: StackObject => StackObject): GameObjectState = {
    updateObject(stack.find(_.objectId == objectId), f)
  }

  def updateLifeTotal(player: PlayerId, f: Int => Int): GameObjectState = {
    Focus[GameObjectState](_.lifeTotals).at(player)(AtGuaranteed.apply).modify(f)(this)
  }

  def addEffects(continuousEffects: Seq[ContinuousEffect], context: InstructionResolutionContext, endCondition: Condition): GameObjectState = {
    updateEffects(_ ++ continuousEffects.map(FloatingActiveContinuousEffect(_, context, endCondition)))
  }
  def updateEffects(f: Seq[FloatingActiveContinuousEffect] => Seq[FloatingActiveContinuousEffect]): GameObjectState = {
    copy(floatingActiveContinuousEffects = f(floatingActiveContinuousEffects))
  }
  def addWaitingTriggeredAbilities(abilities: Seq[Int => PendingTriggeredAbility]): GameObjectState = {
    copy(
      triggeredAbilitiesWaitingToBePutOnStack = triggeredAbilitiesWaitingToBePutOnStack ++ abilities.mapWithIndex { case (ability, index) =>  ability(nextId + index) },
      nextId = nextId + abilities.length)
  }
  def removeTriggeredAbility(ability: PendingTriggeredAbility): GameObjectState = {
    copy(triggeredAbilitiesWaitingToBePutOnStack = triggeredAbilitiesWaitingToBePutOnStack.filter(_ != ability))
  }

  def getCurrentOrLastKnownState(objectId: ObjectId): ObjectWithState = {
    derivedState.allObjectStates.get(objectId) orElse lastKnownInformation.get(objectId) getOrElse { throw new Exception(s"No state found for object $objectId")}
  }
}

object GameObjectState {
  def initial(gameStartingData: GameStartingData, gameData: GameData): GameObjectState = {
    var nextId = 1
    def getNextObjectId = {
      val objectId = ObjectId(nextId)
      nextId += 1
      objectId
    }
    def createGameObject(cardPrinting: CardPrinting, playerIdentifier: PlayerId, zone: BasicZone): BasicGameObject = {
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
      nextId,
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
      Nil,
      Nil)
  }
}
