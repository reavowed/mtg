package mtg.game.turns

import mtg.game.PlayerId
import mtg.game.state.{GameState, InternalGameAction, GameActionResult}
import mtg.game.turns.turnEvents.BeginTurnEvent

case class StartNextTurnAction(playerWithNextTurn: PlayerId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    val turnNumber = gameState.currentTurnNumber + 1
    val nextPlayer = gameState.gameData.getNextPlayerInTurnOrder(playerWithNextTurn)
    Seq(
      BeginTurnEvent(Turn(turnNumber, playerWithNextTurn)),
      StartNextTurnAction(nextPlayer))
  }
  override def canBeReverted: Boolean = false
}
