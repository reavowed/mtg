package mtg.game.turns.turnEvents

import mtg.events.EmptyManaPoolsEvent
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameObjectEvent, GameObjectEventResult, GameState, InternalGameAction, InternalGameActionResult}
import mtg.game.turns.TurnStep

case class EndStepEvent(step: TurnStep) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    EmptyManaPoolsEvent
  }
  override def canBeReverted: Boolean = true
}
