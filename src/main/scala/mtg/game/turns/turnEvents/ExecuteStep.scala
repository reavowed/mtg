package mtg.game.turns.turnEvents

import mtg.actions.EmptyManaPoolsAction
import mtg.game.state.{ExecutableGameAction, GameState, PartialGameActionResult, WrappedOldUpdates}
import mtg.game.turns.TurnStep

case class ExecuteStep(step: TurnStep) extends ExecutableGameAction[Any] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Any] = {
    PartialGameActionResult.children(step.actions :+ WrappedOldUpdates(EmptyManaPoolsAction): _*)
  }
}
