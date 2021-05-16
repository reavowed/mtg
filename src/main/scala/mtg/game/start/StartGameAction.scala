package mtg.game.start

import mtg.game.start.mulligans.DrawAndMulliganAction
import mtg.game.state.{GameActionManager, GameAction, GameState}
import mtg.game.turns.StartNextTurnAction

case object StartGameAction extends GameActionManager {
  override def execute(currentGameState: GameState): Seq[GameAction] = {
    Seq(
      DrawAndMulliganAction(currentGameState.gameData.playersInTurnOrder, 0),
      StartNextTurnAction(currentGameState.gameData.playersInTurnOrder.head))
  }
}
