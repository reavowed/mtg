package mtg.game.turns

import mtg.game.PlayerId
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction, InternalGameActionResult}
import mtg.game.turns.turnEvents.BeginTurnEvent

case class StartNextTurnAction(playerWithNextTurn: PlayerId) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    val turnNumber = currentGameState.currentTurnNumber + 1
    val nextPlayer = currentGameState.gameData.getNextPlayerInTurnOrder(playerWithNextTurn)
    Seq(
      BeginTurnEvent(Turn(turnNumber, playerWithNextTurn)),
      StartNextTurnAction(nextPlayer))
  }
  override def canBeReverted: Boolean = false
}
