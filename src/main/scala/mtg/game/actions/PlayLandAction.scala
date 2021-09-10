package mtg.game.actions

import mtg.events.MoveObjectAction
import mtg.game.objects.GameObject
import mtg.game.state.history.GameEvent
import mtg.game.state.{GameObjectAction, GameObjectActionResult, GameState}
import mtg.game.{PlayerId, Zone}

case class PlayLandAction(player: PlayerId, landCard: GameObject) extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = {
    (MoveObjectAction(player, landCard, Zone.Battlefield), PlayLandEvent)
  }
}

object PlayLandEvent extends GameEvent
