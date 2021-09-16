package mtg.stack.adding

import mtg.game.ObjectId
import mtg.game.objects.AbilityOnTheStack
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class FinishTriggering(abilityId: ObjectId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.derivedState.stackObjectStates.get(abilityId).map[GameActionResult] { ability =>
      val abilityDefinition = ability.gameObject.underlyingObject.asInstanceOf[AbilityOnTheStack].abilityDefinition
      val sourceName = ability.gameObject.underlyingObject.getSourceName(gameState)
      LogEvent.PutTriggeredAbilityOnStack(
        ability.controller,
        sourceName,
        abilityDefinition.effectParagraph.getText(sourceName),
        ability.gameObject.targets.map(_.getName(gameState)))
    }.getOrElse(())
  }

  override def canBeReverted: Boolean = true
}
