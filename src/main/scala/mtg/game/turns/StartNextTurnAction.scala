package mtg.game.turns

import mtg.game.PlayerIdentifier
import mtg.game.state.{GameAction, InternalGameAction, GameState, LogEvent}

case class StartNextTurnAction(playerWithNextTurn: PlayerIdentifier) extends InternalGameAction {
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    val turn = new Turn(playerWithNextTurn)
    val nextPlayer = currentGameState.gameData.getNextPlayerInTurnOrder(playerWithNextTurn)
    (
      Seq(
        BeginTurnEvent(turn),
        StartNextTurnAction(nextPlayer)),
      None
    )
  }
}
