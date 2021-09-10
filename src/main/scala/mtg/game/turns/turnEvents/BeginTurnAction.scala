package mtg.game.turns.turnEvents

import mtg.game.PlayerId
import mtg.game.state.history.{GameEvent, LogEvent}
import mtg.game.state.{GameState, InternalGameAction, InternalGameActionResult}
import mtg.game.turns.{Turn, TurnPhase}

case class BeginTurnAction(player: PlayerId) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    val turn = Turn(currentGameState.currentTurnNumber + 1, player)
    (TurnPhase.All.map(BeginPhaseAction), BeginTurnEvent(turn), LogEvent.NewTurn(player, turn.number))
  }
}

case class BeginTurnEvent(turn: Turn) extends GameEvent
