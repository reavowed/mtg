package mtg.game.start

import mtg.game.state.history.LogEvent
import mtg.game.state.{GameState, PartialGameActionResult, RootGameAction}

object StartGameAction extends RootGameAction {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[RootGameAction] = {
    val startingPlayer = gameState.gameData.playersInTurnOrder.head
    PartialGameActionResult.childThenValue(
      LogEvent.Start(startingPlayer),
      MulligansAction.initial)
  }
}
