package mtg.game.actions

import mtg.events.MoveObjectEvent
import mtg.game.objects.GameObject
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}
import mtg.game.{PlayerIdentifier, Zone}

case class PlayLandEvent(player: PlayerIdentifier, landCard: GameObject) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    MoveObjectEvent(player, landCard, Zone.Battlefield)
  }
}
