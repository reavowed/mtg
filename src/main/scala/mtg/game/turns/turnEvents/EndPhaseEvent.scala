package mtg.game.turns.turnEvents

import mtg.events.EmptyManaPoolsEvent
import mtg.game.state.{GameState, InternalGameAction, InternalGameActionResult}
import mtg.game.turns.TurnPhase

case class EndPhaseEvent(phase: TurnPhase) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    EmptyManaPoolsEvent
  }
  override def canBeReverted: Boolean = false
}
