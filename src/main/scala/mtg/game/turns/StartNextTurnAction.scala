package mtg.game.turns

import mtg.game.PlayerIdentifier
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction, InternalGameActionResult}
import mtg.game.turns.turnEvents.BeginTurnEvent

case class StartNextTurnAction(playerWithNextTurn: PlayerIdentifier) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    val turn = new Turn(playerWithNextTurn)
    val nextPlayer = currentGameState.gameData.getNextPlayerInTurnOrder(playerWithNextTurn)
    Seq(
      BeginTurnEvent(turn),
      StartNextTurnAction(nextPlayer))
  }
}
