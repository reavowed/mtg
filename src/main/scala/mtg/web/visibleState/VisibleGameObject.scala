package mtg.web.visibleState

import mtg.game.PlayerIdentifier
import mtg.game.objects.{CardObject, GameObject, ObjectId}
import mtg.game.state.{Characteristics, GameState, PermanentStatus}
import mtg.game.turns.turnBasedActions.{DeclareAttackers, DeclareBlockers}

case class VisibleGameObject(
  name: String,
  set: String,
  collectorNumber: Int,
  objectId: ObjectId,
  characteristics: Characteristics,
  controller: Option[PlayerIdentifier],
  permanentStatus: Option[PermanentStatus],
  markedDamage: Int,
  modifiers: Map[String, Any])
object VisibleGameObject {
  private def getModifiers(cardObject: CardObject, gameState: GameState): Map[String, Any] = {
    val builder = Map.newBuilder[String, Any]
    if (DeclareAttackers.getAttackDeclarations(gameState).exists(_.attacker == cardObject.objectId)) {
      builder.addOne(("attacking", true))
    }
    DeclareBlockers.getBlockDeclarations(gameState).find(_.blocker == cardObject.objectId).foreach(d => builder.addOne(("blocking", d.attacker)))
    builder.result()
  }

  def apply(gameObject: GameObject, gameState: GameState): VisibleGameObject = gameObject match {
    case cardObject: CardObject =>
      val objectState = gameState.derivedState.objectStates(cardObject.objectId)
      VisibleGameObject(
        cardObject.card.printing.cardDefinition.name,
        cardObject.card.printing.set.code,
        cardObject.card.printing.collectorNumber,
        cardObject.objectId,
        objectState.characteristics,
        objectState.controller,
        cardObject.permanentStatus,
        cardObject.markedDamage,
        getModifiers(cardObject, gameState))
  }
}
