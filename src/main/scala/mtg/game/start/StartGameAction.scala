package mtg.game.start

import mtg.game.state.{AutomaticGameAction, GameAction, GameState}

case object StartGameAction extends AutomaticGameAction {
  override def execute(currentGameState: GameState): (GameState, GameAction) = {
    (currentGameState, DrawStartingHandsAction(currentGameState.gameData.playersInTurnOrder, 0))
  }
}
