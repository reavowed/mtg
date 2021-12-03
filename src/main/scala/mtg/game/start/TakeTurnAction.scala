package mtg.game.start

import mtg.game.state.{GameState, NewGameActionResult, RootGameAction, WrappedOldUpdates}
import mtg.game.turns.Turn
import mtg.game.turns.turnEvents.BeginTurnEvent

case class TakeTurnAction(turn: Turn) extends RootGameAction {
  override def execute()(implicit gameState: GameState): NewGameActionResult.Partial[RootGameAction] = {
    NewGameActionResult.Delegated.valueAfterChild(
      TakeTurnAction(turn.next(gameState)),
      WrappedOldUpdates(BeginTurnEvent(turn))
    )
  }
}

object TakeTurnAction {
  def first(gameState: GameState): TakeTurnAction = {
    TakeTurnAction(Turn(1, gameState.gameData.playersInTurnOrder.head))
  }
}
