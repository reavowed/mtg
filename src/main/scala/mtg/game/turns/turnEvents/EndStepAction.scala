package mtg.game.turns.turnEvents

import mtg.events.EmptyManaPoolsAction
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameObjectAction, GameObjectActionResult, GameState, InternalGameAction, GameActionResult}
import mtg.game.turns.TurnStep

case class EndStepAction(step: TurnStep) extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = {
    EmptyManaPoolsAction
  }
}
