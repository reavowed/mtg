package mtg.game.start

import mtg.game.start.mulligans.DrawAndMulliganAction
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction, InternalGameActionResult}
import mtg.game.turns.StartNextTurnAction

case object StartGameAction extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    val players = currentGameState.gameData.playersInTurnOrder
    val startingPlayer = players.head
    InternalGameActionResult(
      Seq(
        DrawAndMulliganAction(players, 0),
        StartNextTurnAction(startingPlayer)),
      Some(LogEvent.Start(startingPlayer)))
  }
  override def canBeReverted: Boolean = false
}
