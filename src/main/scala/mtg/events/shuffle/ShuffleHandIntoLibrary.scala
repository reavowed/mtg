package mtg.events.shuffle

import mtg.events.MoveObjectAction
import mtg.game.state.{GameObjectAction, GameObjectActionResult, GameState}
import mtg.game.{PlayerId, Zone}

case class ShuffleHandIntoLibrary(player: PlayerId) extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = {
    currentGameState.gameObjectState.hands(player)
      .map(MoveObjectAction(player, _, Zone.Library(player))) :+ ShuffleLibrary(player)
  }
}
