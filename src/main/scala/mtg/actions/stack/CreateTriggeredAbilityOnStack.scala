package mtg.actions.stack

import mtg.abilities.PendingTriggeredAbility
import mtg.core.ObjectId
import mtg.game.objects.StackObject
import mtg.game.state.{DirectGameObjectAction, GameState}

case class CreateTriggeredAbilityOnStack(pendingTriggeredAbility: PendingTriggeredAbility) extends DirectGameObjectAction[ObjectId] {
  override def execute(implicit gameState: GameState): DirectGameObjectAction.Result[ObjectId] = {
    gameState.gameObjectState
      .removeTriggeredAbility(pendingTriggeredAbility)
      .addObjectToStack(StackObject(pendingTriggeredAbility.triggeredAbility.toAbilityOnTheStack, _, pendingTriggeredAbility.triggeredAbility.ownerId))
  }
  override def canBeReverted: Boolean = true
}
