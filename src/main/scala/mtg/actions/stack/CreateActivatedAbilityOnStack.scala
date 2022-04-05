package mtg.actions.stack

import mtg.abilities.ActivatedAbilityDefinition
import mtg.core.{ObjectId, PlayerId}
import mtg.game.objects.{AbilityOnTheStack, StackObject}
import mtg.game.state.{GameActionResult, GameState, GameObjectAction}

case class CreateActivatedAbilityOnStack(ability: ActivatedAbilityDefinition, sourceId: ObjectId, controller: PlayerId) extends GameObjectAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.addObjectToStack(StackObject(AbilityOnTheStack(ability, sourceId, controller), _, controller))
  }

  override def canBeReverted: Boolean = true
}
