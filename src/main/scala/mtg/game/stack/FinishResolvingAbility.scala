package mtg.game.stack

import mtg.abilities.TriggeredAbilityDefinition
import mtg.events.RemoveObjectFromExistenceAction
import mtg.game.objects.AbilityOnTheStack
import mtg.game.state.history.LogEvent
import mtg.game.state.{InternalGameActionResult, GameState, InternalGameAction, StackObjectWithState}

case class FinishResolvingAbility(ability: StackObjectWithState) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    val abilityDefinition = ability.gameObject.underlyingObject.asInstanceOf[AbilityOnTheStack].abilityDefinition
    val description = if (abilityDefinition.isInstanceOf[TriggeredAbilityDefinition]) "triggered" else "activated"
    val sourceName = ability.gameObject.underlyingObject.getSourceName(currentGameState)
    (
      RemoveObjectFromExistenceAction(ability.gameObject.objectId),
      LogEvent.ResolveAbility(
        ability.controller,
        description,
        sourceName,
        abilityDefinition.effectParagraph.getText(sourceName))
    )
  }
}
