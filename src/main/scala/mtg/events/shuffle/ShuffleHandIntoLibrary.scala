package mtg.events.shuffle

import mtg.events.MoveObjectEvent
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}
import mtg.game.{PlayerId, Zone}

case class ShuffleHandIntoLibrary(player: PlayerId) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.hands(player)
      .map(MoveObjectEvent(player, _, Zone.Library(player))) :+ ShuffleLibrary(player)
  }
}
