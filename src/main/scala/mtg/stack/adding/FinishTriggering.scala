package mtg.stack.adding

import mtg.core.ObjectId
import mtg.game.objects.AbilityOnTheStack
import mtg.game.state.history.LogEvent
import mtg.game.state.{CurrentCharacteristics, ExecutableGameAction, GameState, PartialGameActionResult}

case class FinishTriggering(abilityId: ObjectId) extends ExecutableGameAction[Any] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Any] = {
    val abilityWithState = gameState.gameObjectState.derivedState.stackObjectStates(abilityId)
    val underlyingAbilityObject = abilityWithState.gameObject.underlyingObject.asInstanceOf[AbilityOnTheStack]
    val sourceWithState = gameState.gameObjectState.derivedState.allObjectStates(underlyingAbilityObject.source)
    val sourceName = CurrentCharacteristics.getName(sourceWithState)
    PartialGameActionResult.child(
      LogEvent.PutTriggeredAbilityOnStack(
        abilityWithState.controller,
        sourceName,
        underlyingAbilityObject.abilityDefinition.instructions.getText(sourceName),
        abilityWithState.gameObject.targets.map(CurrentCharacteristics.getName(_, gameState))))
  }
}
