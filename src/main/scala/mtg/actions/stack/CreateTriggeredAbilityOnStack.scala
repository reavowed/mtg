package mtg.actions.stack

import mtg.abilities.PendingTriggeredAbility
import mtg.game.objects.StackObject
import mtg.game.state.{GameActionResult, GameState, GameObjectAction}

case class CreateTriggeredAbilityOnStack(pendingTriggeredAbility: PendingTriggeredAbility) extends GameObjectAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState
      .removeTriggeredAbility(pendingTriggeredAbility)
      .addObjectToStack(StackObject(pendingTriggeredAbility.triggeredAbility.toAbilityOnTheStack, _, pendingTriggeredAbility.triggeredAbility.ownerId))
  }
  override def canBeReverted: Boolean = true
}
