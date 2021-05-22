package mtg.game.actions

import mtg.game.PlayerIdentifier
import mtg.game.objects.ManaObject
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class SpendManaAutomaticallyEvent(player: PlayerIdentifier, remainingMana: Seq[ManaObject]) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.updateManaPool(player, _ => remainingMana)
  }
}
