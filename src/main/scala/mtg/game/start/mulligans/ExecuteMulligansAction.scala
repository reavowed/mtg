package mtg.game.start.mulligans

import mtg.events.shuffle.ShuffleHandIntoLibrary
import mtg.game.state.history.GameEvent
import mtg.game.state.{GameState, InternalGameAction, InternalGameActionResult}

case class ExecuteMulligansAction(mulligansSoFar: Int) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    val playersMulliganing = currentGameState.gameHistory.gameEventsThisTurn.since[DrawStartingHandsEvent].collect {
      case GameEvent.Decision(MulliganOption.Mulligan, player) => player
    }.toSeq.reverse
    if (playersMulliganing.nonEmpty) {
      playersMulliganing.map(ShuffleHandIntoLibrary) :+ DrawAndMulliganAction(playersMulliganing, mulligansSoFar + 1)
    } else {
      ()
    }
  }
  override def canBeReverted: Boolean = false
}
