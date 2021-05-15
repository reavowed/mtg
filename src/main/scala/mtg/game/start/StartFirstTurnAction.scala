package mtg.game.start

import mtg.game.state.{AutomaticGameAction, GameState}
import mtg.game.turns.PriorityChoice

object StartFirstTurnAction extends AutomaticGameAction {
  override def execute(currentGameState: GameState): GameState = {
    currentGameState.updateTransition(PriorityChoice(currentGameState.gameData.playersInTurnOrder.head))
  }
}
