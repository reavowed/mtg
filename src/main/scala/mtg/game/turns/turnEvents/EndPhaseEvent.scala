package mtg.game.turns.turnEvents

import mtg.events.EmptyManaPoolsEvent
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}
import mtg.game.turns.TurnPhase

case class EndPhaseEvent(phase: TurnPhase) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    EmptyManaPoolsEvent
  }
  override def canBeReverted: Boolean = false
}
