package mtg.stack.adding

import mtg.game.ObjectId
import mtg.game.objects.AbilityOnTheStack
import mtg.game.state.history.LogEvent
import mtg.game.state.{CurrentCharacteristics, ExecutableGameAction, GameState, PartialGameActionResult}

case class FinishActivating(abilityId: ObjectId) extends ExecutableGameAction[Any] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Any] = {
    val ability = gameState.gameObjectState.derivedState.stackObjectStates(abilityId)
    val abilityDefinition = ability.gameObject.underlyingObject.asInstanceOf[AbilityOnTheStack].abilityDefinition
    val sourceName = ability.gameObject.underlyingObject.getSourceName(gameState)
    PartialGameActionResult.child(
      LogEvent.ActivateAbility(
        ability.controller,
        sourceName,
        abilityDefinition.effectParagraph.getText(sourceName),
        ability.gameObject.targets.map(CurrentCharacteristics.getName(_, gameState))))
  }
}