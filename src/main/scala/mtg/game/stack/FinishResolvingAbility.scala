package mtg.game.stack

import mtg.abilities.TriggeredAbilityDefinition
import mtg.events.RemoveObjectFromExistenceEvent
import mtg.game.objects.AbilityOnTheStack
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameState, InternalGameAction, GameActionResult, StackObjectWithState}

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
