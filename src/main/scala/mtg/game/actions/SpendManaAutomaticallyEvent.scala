package mtg.game.actions

import mtg.game.PlayerId
import mtg.game.objects.ManaObject
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class SpendManaAutomaticallyEvent(player: PlayerId, remainingMana: Seq[ManaObject]) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.updateManaPool(player, _ => remainingMana)
  }
  override def canBeReverted: Boolean = true
}
