package mtg.game.turns.turnEvents

import mtg.events.EmptyManaPoolsEvent
import mtg.game.state.{ExecutableGameAction, GameState, PartialGameActionResult, WrappedOldUpdates}
import mtg.game.turns.TurnPhase

case class ExecutePhase(phase: TurnPhase) extends ExecutableGameAction[Any] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Any] = {
    PartialGameActionResult.children(phase.actions :+ WrappedOldUpdates(EmptyManaPoolsEvent))
  }
}
