package mtg.game.start

import mtg.game.start.mulligans.DrawAndMulliganAction
import mtg.game.state.{GameAction, InternalGameAction, GameState, LogEvent}
import mtg.game.turns.StartNextTurnAction

case object StartGameAction extends InternalGameAction {
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    val players = currentGameState.gameData.playersInTurnOrder
    val startingPlayer = players.head
    (
      Seq(
        DrawAndMulliganAction(players, 0),
        StartNextTurnAction(startingPlayer)),
      Some(LogEvent.Start(startingPlayer))
    )
  }
}
