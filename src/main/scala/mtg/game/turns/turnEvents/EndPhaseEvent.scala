package mtg.game.turns.turnEvents

import mtg.events.EmptyManaPoolsEvent
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameObjectEvent, GameObjectEventResult, GameState, InternalGameAction, GameActionResult}
import mtg.game.turns.TurnPhase

case class EndPhaseEvent(phase: TurnPhase) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    EmptyManaPoolsEvent
  }
}
