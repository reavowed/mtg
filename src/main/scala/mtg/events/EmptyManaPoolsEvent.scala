package mtg.events

import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

object EmptyManaPoolsEvent extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.playersInApnapOrder.foldLeft(currentGameState.gameObjectState) { (gameObjectState, player) =>
      gameObjectState.updateManaPool(player, _ => Nil)
    }
  }
}
