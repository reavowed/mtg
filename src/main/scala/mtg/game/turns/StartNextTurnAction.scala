package mtg.game.turns

import mtg.game.PlayerId
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction, GameActionResult}
import mtg.game.turns.turnEvents.BeginTurnEvent

case class StartNextTurnAction(playerWithNextTurn: PlayerId) extends InternalGameAction {
  override def execute(currentGameState: GameState): GameActionResult = {
    val turn = new Turn(playerWithNextTurn)
    val nextPlayer = currentGameState.gameData.getNextPlayerInTurnOrder(playerWithNextTurn)
    Seq(
      BeginTurnEvent(turn),
      StartNextTurnAction(nextPlayer))
  }
}
