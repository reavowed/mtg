package mtg.stack.resolving

import mtg.abilities.TriggeredAbilityDefinition
import mtg.actions.RemoveObjectFromExistenceEvent
import mtg.game.objects.AbilityOnTheStack
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameActionResult, GameState, InternalGameAction, StackObjectWithState}

case class FinishResolvingAbility(ability: StackObjectWithState) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    val abilityDefinition = ability.gameObject.underlyingObject.asInstanceOf[AbilityOnTheStack].abilityDefinition
    val description = if (abilityDefinition.isInstanceOf[TriggeredAbilityDefinition]) "triggered" else "activated"
    val sourceName = ability.gameObject.underlyingObject.getSourceName(gameState)
    (
      RemoveObjectFromExistenceEvent(ability.gameObject.objectId),
      LogEvent.ResolveAbility(
        ability.controller,
        description,
        sourceName,
        abilityDefinition.effectParagraph.getText(sourceName))
    )
  }

  override def canBeReverted: Boolean = false
}
