package mtg.game.stack.steps

import mtg.game.ObjectId
import mtg.game.objects.AbilityOnTheStack
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameState, InternalGameAction, InternalGameActionResult}

case class FinishTriggering(abilityId: ObjectId) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    currentGameState.gameObjectState.derivedState.spellStates.get(abilityId).map[InternalGameActionResult] { ability =>
      val abilityDefinition = ability.gameObject.underlyingObject.asInstanceOf[AbilityOnTheStack].abilityDefinition
      val sourceName = ability.gameObject.underlyingObject.getSourceName(currentGameState)
      LogEvent.PutTriggeredAbilityOnStack(
        ability.controller,
        sourceName,
        abilityDefinition.effectParagraph.getText(sourceName),
        ability.gameObject.targets.map(_.getName(currentGameState)))
    }.getOrElse(())
  }
  override def canBeReverted: Boolean = true
}
