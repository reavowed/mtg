package mtg.game.turns.turnBasedActions

import mtg.events.DrawCardEvent
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction, GameActionResult}

case object DrawForTurn extends InternalGameAction {
  override def execute(currentGameState: GameState): GameActionResult = {
    GameActionResult(Seq(DrawCardEvent(currentGameState.activePlayer)), Some(LogEvent.DrawForTurn(currentGameState.activePlayer)))
  }
}
