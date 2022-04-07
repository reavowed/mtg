package mtg.actions.stack

import mtg.abilities.PendingTriggeredAbility
import mtg.game.objects.{GameObjectState, StackObject}
import mtg.game.state.{DirectGameObjectAction, GameState}

case class CreateTriggeredAbilityOnStack(pendingTriggeredAbility: PendingTriggeredAbility) extends DirectGameObjectAction {
  override def execute(implicit gameState: GameState): GameObjectState = {
    gameState.gameObjectState
      .removeTriggeredAbility(pendingTriggeredAbility)
      .addObjectToStack(StackObject(pendingTriggeredAbility.triggeredAbility.toAbilityOnTheStack, _, pendingTriggeredAbility.triggeredAbility.ownerId))
  }
  override def canBeReverted: Boolean = true
}
