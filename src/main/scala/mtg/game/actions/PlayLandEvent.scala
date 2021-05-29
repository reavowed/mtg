package mtg.game.actions

import mtg.events.MoveObjectEvent
import mtg.game.objects.GameObject
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}
import mtg.game.{PlayerId, Zone}

case class PlayLandEvent(player: PlayerId, landCard: GameObject) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    MoveObjectEvent(player, landCard, Zone.Battlefield)
  }
}
