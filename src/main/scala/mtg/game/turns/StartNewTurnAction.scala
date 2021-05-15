package mtg.game.turns

import mtg.game.state.{GameActionManager, GameAction, GameState}

case object StartNewTurnAction extends GameActionManager {
  override def execute(currentGameState: GameState): Seq[GameAction] = {
    Seq(PriorityChoice(currentGameState.gameData.playersInTurnOrder.head), StartNewTurnAction)
  }
}
