package mtg.web.visibleState

import mtg.core.{ObjectId, PlayerId}
import mtg.game.objects._
import mtg.game.state.{Characteristics, GameState, PermanentStatus}
import mtg.game.turns.turnBasedActions.{DeclareAttackers, DeclareBlockers}

sealed trait PossiblyHiddenGameObject

case object HiddenGameObject extends PossiblyHiddenGameObject

case class VisibleGameObject(
    name: Option[String],
    artDetails: ArtDetails,
    objectId: ObjectId,
    characteristics: Characteristics,
    text: String,
    owner: PlayerId,
    controller: Option[PlayerId],
    permanentStatus: Option[PermanentStatus],
    markedDamage: Option[Int],
    counters: Map[String, Int],
    modifiers: Map[String, Any])
  extends PossiblyHiddenGameObject

object VisibleGameObject {
  private def getModifiers(gameObject: GameObject, gameState: GameState): Map[String, Any] = {
    val builder = Map.newBuilder[String, Any]
    if (DeclareAttackers.isAttacking(gameObject.objectId, gameState)) {
      builder.addOne(("attacking", true))
    }
    DeclareBlockers.getAttackerForBlocker(gameObject.objectId, gameState).foreach(attacker =>
      builder.addOne(("blocking", attacker))
    )
    builder.result()
  }
  private def getCounters(gameObject: GameObject): Map[String, Int] = {
    gameObject.counters.map(_.mapLeft(_.description))
  }

  def apply(gameObject: GameObject, gameState: GameState): VisibleGameObject = gameObject match {
    case gameObject: BasicGameObject =>
      val objectState = gameState.gameObjectState.derivedState.basicStates(gameObject.objectId)
      VisibleGameObject(
        objectState.characteristics.name,
        ArtDetails.get(gameObject.underlyingObject, gameState),
        gameObject.objectId,
        objectState.characteristics,
        objectState.getText(gameState),
        objectState.gameObject.underlyingObject.owner,
        None,
        None,
        None,
        getCounters(gameObject),
        getModifiers(gameObject, gameState))
    case gameObject: StackObject =>
      val objectState = gameState.gameObjectState.derivedState.stackObjectStates(gameObject.objectId)
      VisibleGameObject(
        objectState.characteristics.name,
        ArtDetails.get(gameObject.underlyingObject, gameState),
        gameObject.objectId,
        objectState.characteristics,
        objectState.getText(gameState),
        objectState.gameObject.underlyingObject.owner,
        Some(objectState.controller),
        None,
        None,
        getCounters(gameObject),
        getModifiers(gameObject, gameState))
    case gameObject: PermanentObject =>
      val objectState = gameState.gameObjectState.derivedState.permanentStates(gameObject.objectId)
      VisibleGameObject(
        objectState.characteristics.name,
        ArtDetails.get(gameObject.underlyingObject, gameState),
        gameObject.objectId,
        objectState.characteristics,
        objectState.getText(gameState),
        objectState.gameObject.underlyingObject.owner,
        Some(objectState.controller),
        Some(gameObject.status),
        Some(gameObject.markedDamage),
        getCounters(gameObject),
        getModifiers(gameObject, gameState))
  }
}
