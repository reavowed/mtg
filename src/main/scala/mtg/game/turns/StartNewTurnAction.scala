package mtg.game.turns

import mtg.game.state.{AutomaticGameAction, GameAction, GameState}

case object StartNewTurnAction extends AutomaticGameAction {
  override def execute(currentGameState: GameState): (GameState, Seq[GameAction]) = {
    (currentGameState, Seq(PriorityChoice(currentGameState.gameData.playersInTurnOrder.head)))
  }
}
