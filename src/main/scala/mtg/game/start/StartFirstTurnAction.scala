package mtg.game.start

import mtg.game.state.{Action, GameState}
import mtg.game.turns.PriorityChoice

object StartFirstTurnAction extends Action {
  override def runAction(currentGameState: GameState): GameState = {
    currentGameState.updateTransition(PriorityChoice(currentGameState.gameData.playersInTurnOrder.head))
  }
}
