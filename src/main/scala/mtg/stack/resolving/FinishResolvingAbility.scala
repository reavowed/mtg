package mtg.stack.resolving

import mtg.abilities.TriggeredAbilityDefinition
import mtg.actions.RemoveObjectFromExistenceAction
import mtg.game.objects.AbilityOnTheStack
import mtg.game.state.history.LogEvent
import mtg.game.state.{CurrentCharacteristics, ExecutableGameAction, GameActionResult, GameState, InternalGameAction, PartialGameActionResult, StackObjectWithState, WrappedOldUpdates}

case class FinishResolvingAbility(ability: StackObjectWithState) extends ExecutableGameAction[Unit] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    val underlyingAbilityObject = ability.gameObject.underlyingObject.asInstanceOf[AbilityOnTheStack]
    val abilityDefinition = underlyingAbilityObject.abilityDefinition
    val description = if (underlyingAbilityObject.abilityDefinition.isInstanceOf[TriggeredAbilityDefinition]) "triggered" else "activated"
    val sourceWithState = gameState.gameObjectState.derivedState.allObjectStates(underlyingAbilityObject.source)
    val sourceName = CurrentCharacteristics.getName(sourceWithState)
    PartialGameActionResult.childrenThenValue(
      Seq(
        WrappedOldUpdates(RemoveObjectFromExistenceAction(ability.gameObject.objectId)),
        LogEvent.ResolveAbility(
          ability.controller,
          description,
          sourceName,
          abilityDefinition.instructions.getText(sourceName))),
      ())
  }
}
