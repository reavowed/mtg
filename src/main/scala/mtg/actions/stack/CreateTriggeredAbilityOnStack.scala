package mtg.actions.stack

import mtg.abilities.PendingTriggeredAbility
import mtg.definitions.ObjectId
import mtg.game.objects.StackObject
import mtg.game.state.{DirectGameObjectAction, GameState}

case class CreateTriggeredAbilityOnStack(pendingTriggeredAbility: PendingTriggeredAbility) extends DirectGameObjectAction[ObjectId] {
  override def execute(implicit gameState: GameState): DirectGameObjectAction.Result[ObjectId] = {
    gameState.gameObjectState
      .removeTriggeredAbility(pendingTriggeredAbility)
      .addObjectToStack(StackObject(pendingTriggeredAbility.toAbilityOnTheStack, _, pendingTriggeredAbility.triggeredAbility.controllerId))
  }
  override def canBeReverted: Boolean = true
}
