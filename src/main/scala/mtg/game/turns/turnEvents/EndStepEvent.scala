package mtg.game.turns.turnEvents

import mtg.events.EmptyManaPoolsEvent
import mtg.game.state.{InternalGameAction, GameActionResult, GameState}
import mtg.game.turns.TurnStep

case class EndStepEvent(step: TurnStep) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    EmptyManaPoolsEvent
  }
  override def canBeReverted: Boolean = true
}
