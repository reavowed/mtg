package mtg.game.turns.turnEvents

import mtg.game.state.history.LogEvent
import mtg.game.state.{GameState, PartialGameActionResult, RootGameAction, WrappedOldUpdates}
import mtg.game.turns.{Turn, TurnPhase}

case class TakeTurnAction(turn: Turn) extends RootGameAction {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[RootGameAction] = {
    PartialGameActionResult.childrenThenValue(
      Seq(LogEvent.NewTurn(turn), WrappedOldUpdates(TurnPhase.All.map(BeginPhaseEvent): _*)),
      TakeTurnAction(turn.next(gameState)))
  }
}

object TakeTurnAction {
  def first(gameState: GameState): TakeTurnAction = {
    TakeTurnAction(Turn(1, gameState.gameData.playersInTurnOrder.head))
  }
}
