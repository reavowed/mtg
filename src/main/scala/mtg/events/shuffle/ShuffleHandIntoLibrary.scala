package mtg.events.shuffle

import mtg.events.MoveObjectEvent
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}
import mtg.game.{PlayerIdentifier, Zone}

case class ShuffleHandIntoLibrary(playerIdentifier: PlayerIdentifier) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.hands(playerIdentifier)
      .map(MoveObjectEvent(_, Zone.Library(playerIdentifier))) :+ ShuffleLibrary(playerIdentifier)
  }
}
