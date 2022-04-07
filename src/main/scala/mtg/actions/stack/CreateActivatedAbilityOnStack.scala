package mtg.actions.stack

import mtg.abilities.ActivatedAbilityDefinition
import mtg.core.{ObjectId, PlayerId}
import mtg.game.objects.{AbilityOnTheStack, GameObjectState, StackObject}
import mtg.game.state.{DirectGameObjectAction, GameState}

case class CreateActivatedAbilityOnStack(ability: ActivatedAbilityDefinition, sourceId: ObjectId, controller: PlayerId) extends DirectGameObjectAction[Unit] {
  override def execute(implicit gameState: GameState): DirectGameObjectAction.Result[Unit] = {
    gameState.gameObjectState.addObjectToStack(StackObject(AbilityOnTheStack(ability, sourceId, controller), _, controller))
  }
  override def canBeReverted: Boolean = true
}
