package mtg.stack.resolving

import mtg.abilities.TriggeredAbilityDefinition
import mtg.actions.RemoveObjectFromExistenceAction
import mtg.game.objects.AbilityOnTheStack
import mtg.game.state.history.LogEvent
import mtg.game.state._

case class FinishResolvingAbility(ability: StackObjectWithState) extends DelegatingGameAction[Unit] {
  override def delegate(implicit gameState: GameState): GameAction[Unit] = {
    val underlyingAbilityObject = ability.gameObject.underlyingObject.asInstanceOf[AbilityOnTheStack]
    val abilityDefinition = underlyingAbilityObject.abilityDefinition
    val description = if (underlyingAbilityObject.abilityDefinition.isInstanceOf[TriggeredAbilityDefinition]) "triggered" else "activated"
    val sourceWithState = gameState.gameObjectState.getCurrentOrLastKnownState(underlyingAbilityObject.source)
    val sourceName = CurrentCharacteristics.getName(sourceWithState)
    RemoveObjectFromExistenceAction(ability.gameObject.objectId)
      .andThen(LogEvent.ResolveAbility(ability.controller, description, sourceName, abilityDefinition.instructions.getText(sourceName)))
  }
}
