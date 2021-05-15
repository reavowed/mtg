package mtg.game.start

import mtg.game.state.{AutomaticGameAction, GameState}

object StartMulliganProcessAction extends AutomaticGameAction {
  def execute(currentGameState: GameState): GameState = {
    currentGameState.updateTransition(DrawStartingHandsAction(currentGameState.gameData.playersInTurnOrder, 0))
  }
}
