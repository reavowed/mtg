package mtg.game.turns.turnEvents

import mtg.events.EmptyManaPoolsAction
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameObjectAction, GameObjectActionResult, GameState, InternalGameAction, GameActionResult}
import mtg.game.turns.TurnPhase

case class EndPhaseAction(phase: TurnPhase) extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = {
    EmptyManaPoolsAction
  }
}
