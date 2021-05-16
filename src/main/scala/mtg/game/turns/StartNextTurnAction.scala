package mtg.game.turns

import mtg.game.PlayerIdentifier
import mtg.game.state.{GameAction, GameActionManager, GameState}

case class StartNextTurnAction(playerWithNextTurn: PlayerIdentifier) extends GameActionManager {
  override def execute(currentGameState: GameState): Seq[GameAction] = {
    Seq(
      PriorityChoice(currentGameState.gameData.getPlayersInApNapOrder(playerWithNextTurn)),
      StartNextTurnAction(currentGameState.gameData.getNextPlayerInTurnOrder(playerWithNextTurn)))
  }
}
