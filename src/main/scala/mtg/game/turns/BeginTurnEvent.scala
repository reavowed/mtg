package mtg.game.turns

import mtg.game.state._

case class BeginTurnEvent(turn: Turn) extends TurnCycleEvent {
  override def execute(currentGameState: GameState): (GameHistory, Seq[GameAction], LogEvent) = {
    val newTurnNumber = currentGameState.currentTurnNumber + 1
    (
      currentGameState.gameHistory.startTurn(turn),
      Seq(PriorityChoice(currentGameState.gameData.getPlayersInApNapOrder(turn.activePlayer))),
      LogEvent.NewTurn(turn.activePlayer, newTurnNumber)
    )
  }
}
