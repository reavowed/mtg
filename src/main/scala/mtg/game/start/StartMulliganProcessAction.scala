package mtg.game.start

import mtg.game.state.{Action, GameState}

object StartMulliganProcessAction extends Action {
  def runAction(currentGameState: GameState): GameState = {
    currentGameState.updateTransition(DrawStartingHandsAction(currentGameState.gameData.playersInTurnOrder, 0))
  }
}
