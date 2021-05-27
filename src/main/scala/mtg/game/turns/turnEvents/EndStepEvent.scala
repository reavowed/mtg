package mtg.game.turns.turnEvents

import mtg.events.EmptyManaPoolsEvent
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction, InternalGameActionResult}
import mtg.game.turns.TurnStep

case class EndStepEvent(step: TurnStep) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    EmptyManaPoolsEvent
  }
}
