package mtg.game.start

import mtg.game.state.{AutomaticGameAction, GameAction, GameState}

object StartMulliganProcessAction extends AutomaticGameAction {
  def execute(currentGameState: GameState): (GameState, GameAction) = {
    (currentGameState, DrawStartingHandsAction(currentGameState.gameData.playersInTurnOrder, 0))
  }
}
