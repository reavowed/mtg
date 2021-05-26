package mtg.web.visibleState

import mtg.game.objects.{CardObject, GameObject, ObjectId}
import mtg.game.state.{Characteristics, GameState, PermanentStatus}
import mtg.game.turns.turnBasedActions.{DeclareAttackers, DeclareBlockers}

sealed trait VisibleGameObject
object VisibleGameObject {
  def apply(gameObject: GameObject, gameState: GameState): VisibleGameObject = gameObject match {
    case cardObject: CardObject => VisibleCard(cardObject, gameState)
  }
}

case class VisibleCard(
  name: String,
  set: String,
  collectorNumber: Int,
  objectId: ObjectId,
  characteristics: Characteristics,
  permanentStatus: Option[PermanentStatus],
  markedDamage: Int,
  modifiers: Map[String, Any]
) extends VisibleGameObject

object VisibleCard {
  def getModifiers(cardObject: CardObject, gameState: GameState): Map[String, Any] = {
    val builder = Map.newBuilder[String, Any]
    if (DeclareAttackers.getAttackDeclarations(gameState).exists(_.attacker == cardObject.objectId)) {
      builder.addOne(("attacking", true))
    }
    DeclareBlockers.getBlockDeclarations(gameState).find(_.blocker == cardObject.objectId).foreach(d => builder.addOne(("blocking", d.blockedCreature)))
    builder.result()
  }

  def apply(cardObject: CardObject, gameState: GameState): VisibleCard = VisibleCard(
    cardObject.card.printing.cardDefinition.name,
    cardObject.card.printing.set.code,
    cardObject.card.printing.collectorNumber,
    cardObject.objectId,
    gameState.derivedState.objectStates(cardObject.objectId).characteristics,
    cardObject.permanentStatus,
    cardObject.markedDamage,
    getModifiers(cardObject, gameState))
}
