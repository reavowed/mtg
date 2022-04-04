package mtg.game.turns.turnEvents

import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, PartialGameActionResult, RootGameAction}
import mtg.game.turns.{Turn, TurnPhase}

case class ExecuteTurn(turn: Turn) extends RootGameAction {
  override def delegate(implicit gameState: GameState): GameAction[RootGameAction] = {
    for {
      _ <- LogEvent.NewTurn(turn)
      _ <- TurnPhase.All.map(ExecutePhase).traverse
    } yield ExecuteTurn(turn.next(gameState))
  }
}

object ExecuteTurn {
  def first(implicit gameState: GameState): ExecuteTurn = {
    ExecuteTurn(Turn(1, gameState.gameData.playersInTurnOrder.head))
  }
}
