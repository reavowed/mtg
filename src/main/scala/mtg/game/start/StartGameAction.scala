package mtg.game.start

import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, PartialGameActionResult, RootGameAction}

object StartGameAction extends RootGameAction {
  override def delegate(implicit gameState: GameState): GameAction[RootGameAction] = {
    val startingPlayer = gameState.gameData.playersInTurnOrder.head
    for {
      _ <- LogEvent.Start(startingPlayer)
    } yield MulligansAction.initial
  }
}
