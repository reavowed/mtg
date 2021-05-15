package mtg.game.start

import mtg.game.state.{AutomaticGameAction, GameState}

object StartMulliganProcessAction extends AutomaticGameAction {
  def execute(currentGameState: GameState): GameState = {
    currentGameState.updateAction(DrawStartingHandsAction(currentGameState.gameData.playersInTurnOrder, 0))
  }
}
