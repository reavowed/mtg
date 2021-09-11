package mtg.game.turns.turnBasedActions

import mtg.events.DrawCardEvent
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction, InternalGameActionResult}

case object DrawForTurn extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    InternalGameActionResult(Seq(DrawCardEvent(currentGameState.activePlayer)), Some(LogEvent.DrawForTurn(currentGameState.activePlayer)))
  }
}
