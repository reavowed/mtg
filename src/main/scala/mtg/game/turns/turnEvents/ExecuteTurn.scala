package mtg.game.turns.turnEvents

import mtg.game.state.history.LogEvent
import mtg.game.state.{GameState, PartialGameActionResult, RootGameAction}
import mtg.game.turns.{Turn, TurnPhase}

case class ExecuteTurn(turn: Turn) extends RootGameAction {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[RootGameAction] = {
    PartialGameActionResult.childrenThenValue(
      LogEvent.NewTurn(turn) +: TurnPhase.All.map(ExecutePhase),
      ExecuteTurn(turn.next(gameState)))
  }
}

object ExecuteTurn {
  def first(gameState: GameState): ExecuteTurn = {
    ExecuteTurn(Turn(1, gameState.gameData.playersInTurnOrder.head))
  }
}
