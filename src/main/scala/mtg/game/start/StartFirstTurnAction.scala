package mtg.game.start

import mtg.game.state.{AutomaticGameAction, GameAction, GameState}
import mtg.game.turns.PriorityChoice

object StartFirstTurnAction extends AutomaticGameAction {
  override def execute(currentGameState: GameState): (GameState, GameAction) = {
    (currentGameState, PriorityChoice(currentGameState.gameData.playersInTurnOrder.head))
  }
}
