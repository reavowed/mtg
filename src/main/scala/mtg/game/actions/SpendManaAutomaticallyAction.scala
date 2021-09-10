package mtg.game.actions

import mtg.game.PlayerId
import mtg.game.objects.ManaObject
import mtg.game.state.{GameObjectAction, GameObjectActionResult, GameState}

case class SpendManaAutomaticallyAction(player: PlayerId, remainingMana: Seq[ManaObject]) extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = {
    currentGameState.gameObjectState.updateManaPool(player, _ => remainingMana)
  }
}
