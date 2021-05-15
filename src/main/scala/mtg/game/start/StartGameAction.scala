package mtg.game.start

import mtg.game.start.mulligans.DrawAndMulliganAction
import mtg.game.state.{AutomaticGameAction, GameAction, GameState}
import mtg.game.turns.StartNewTurnAction

case object StartGameAction extends AutomaticGameAction {
  override def execute(currentGameState: GameState): (GameState, Seq[GameAction]) = {
    (currentGameState, Seq(DrawAndMulliganAction(currentGameState.gameData.playersInTurnOrder, 0), StartNewTurnAction))
  }
}
