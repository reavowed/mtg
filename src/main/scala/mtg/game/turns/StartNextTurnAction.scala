package mtg.game.turns

import mtg.game.PlayerId
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction, InternalGameActionResult}
import mtg.game.turns.turnEvents.BeginTurnAction

case class StartNextTurnAction(playerWithNextTurn: PlayerId) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    val turn = new Turn(playerWithNextTurn)
    val nextPlayer = currentGameState.gameData.getNextPlayerInTurnOrder(playerWithNextTurn)
    Seq(
      BeginTurnAction(turn),
      StartNextTurnAction(nextPlayer))
  }
}
