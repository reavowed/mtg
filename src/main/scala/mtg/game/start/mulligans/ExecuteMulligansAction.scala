package mtg.game.start.mulligans

import mtg.events.shuffle.ShuffleHandIntoLibrary
import mtg.game.state.history.GameEvent
import mtg.game.state.{GameState, InternalGameAction, GameActionResult}

case class ExecuteMulligansAction(mulligansSoFar: Int) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    val playersMulliganing = gameState.gameHistory.gameEventsThisTurn.since[DrawStartingHandsEvent]
      .actions.ofType[MulliganDecision.Mulligan]
      .map(_.player).toSeq.reverse

    if (playersMulliganing.nonEmpty) {
      playersMulliganing.map(ShuffleHandIntoLibrary) :+ DrawAndMulliganAction(playersMulliganing, mulligansSoFar + 1)
    } else {
      ()
    }
  }
  override def canBeReverted: Boolean = false
}
