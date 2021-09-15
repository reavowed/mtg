package mtg.game.start

import mtg.game.start.mulligans.DrawAndMulliganAction
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameState, InternalGameAction, GameActionResult}
import mtg.game.turns.StartNextTurnAction

case object StartGameAction extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    val players = gameState.gameData.playersInTurnOrder
    val startingPlayer = players.head
    (
      Seq(
        DrawAndMulliganAction(players, 0),
        StartNextTurnAction(startingPlayer)),
      LogEvent.Start(startingPlayer)
    )
  }
  override def canBeReverted: Boolean = false
}
