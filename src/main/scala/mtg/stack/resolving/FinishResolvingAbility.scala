package mtg.stack.resolving

import mtg.abilities.TriggeredAbilityDefinition
import mtg.actions.RemoveObjectFromExistenceAction
import mtg.game.objects.AbilityOnTheStack
import mtg.game.state.history.LogEvent
import mtg.game.state.{ExecutableGameAction, GameActionResult, GameState, InternalGameAction, PartialGameActionResult, StackObjectWithState, WrappedOldUpdates}

case class FinishResolvingAbility(ability: StackObjectWithState) extends ExecutableGameAction[Unit] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    val abilityDefinition = ability.gameObject.underlyingObject.asInstanceOf[AbilityOnTheStack].abilityDefinition
    val description = if (abilityDefinition.isInstanceOf[TriggeredAbilityDefinition]) "triggered" else "activated"
    val sourceName = ability.gameObject.underlyingObject.getSourceName(gameState)
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
