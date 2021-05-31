package mtg.web.visibleState

import mtg.game.objects.{BasicGameObject, GameObject, PermanentObject, StackObject}
import mtg.game.state.{Characteristics, GameState, PermanentStatus}
import mtg.game.turns.turnBasedActions.{DeclareAttackers, DeclareBlockers}
import mtg.game.{ObjectId, PlayerId}

case class VisibleGameObject(
  name: String,
  set: String,
  collectorNumber: Int,
  objectId: ObjectId,
  characteristics: Characteristics,
  text: String,
  controller: Option[PlayerId],
  permanentStatus: Option[PermanentStatus],
  markedDamage: Option[Int],
  modifiers: Map[String, Any])
object VisibleGameObject {
  private def getModifiers(gameObject: GameObject, gameState: GameState): Map[String, Any] = {
    val builder = Map.newBuilder[String, Any]
    if (DeclareAttackers.isAttacking(gameObject.objectId, gameState)) {
      builder.addOne(("attacking", true))
    }
    if (DeclareBlockers.isBlocking(gameObject.objectId, gameState)) {
      builder.addOne(("blocking", true))
    }
    builder.result()
  }

  def apply(gameObject: GameObject, gameState: GameState): VisibleGameObject = gameObject match {
    case gameObject: BasicGameObject =>
      val objectState = gameState.gameObjectState.derivedState.basicStates(gameObject.objectId)
      VisibleGameObject(
        gameObject.card.printing.cardDefinition.name,
        gameObject.card.printing.set.code,
        gameObject.card.printing.collectorNumber,
        gameObject.objectId,
        objectState.characteristics,
        gameObject.card.printing.cardDefinition.text,
        None,
        None,
        None,
        getModifiers(gameObject, gameState))
    case gameObject: StackObject =>
      val objectState = gameState.gameObjectState.derivedState.spellStates(gameObject.objectId)
      VisibleGameObject(
        gameObject.card.printing.cardDefinition.name,
        gameObject.card.printing.set.code,
        gameObject.card.printing.collectorNumber,
        gameObject.objectId,
        objectState.characteristics,
        gameObject.card.printing.cardDefinition.text,
        Some(objectState.controller),
        None,
        None,
        getModifiers(gameObject, gameState))
    case gameObject: PermanentObject =>
      val objectState = gameState.gameObjectState.derivedState.permanentStates(gameObject.objectId)
      VisibleGameObject(
        gameObject.card.printing.cardDefinition.name,
        gameObject.card.printing.set.code,
        gameObject.card.printing.collectorNumber,
        gameObject.objectId,
        objectState.characteristics,
        gameObject.card.printing.cardDefinition.text,
        Some(objectState.controller),
        Some(gameObject.status),
        Some(gameObject.markedDamage),
        getModifiers(gameObject, gameState))
  }
}
