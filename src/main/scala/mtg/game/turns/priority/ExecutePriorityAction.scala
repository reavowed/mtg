package mtg.game.turns.priority

import mtg.game.actions.PriorityAction
import mtg.game.state.{BackupAction, ExecutableGameAction, GameState, PartialGameActionResult}

case class ExecutePriorityAction(priorityAction: PriorityAction, backupAction: BackupAction) extends ExecutableGameAction[Any] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Any] = {
    priorityAction.execute(backupAction)
  }
}
