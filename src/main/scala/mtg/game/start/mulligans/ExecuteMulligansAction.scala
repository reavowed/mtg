package mtg.game.start.mulligans

import mtg.events.shuffle.ShuffleHandIntoLibrary
import mtg.game.state.history.{GameEvent, LogEvent}
import mtg.game.state.{GameAction, GameState, InternalGameAction, GameActionResult}

case class ExecuteMulligansAction(mulligansSoFar: Int) extends InternalGameAction {
  override def execute(currentGameState: GameState): GameActionResult = {
    val playersMulliganing = currentGameState.gameHistory.preGameEvents.sinceEvent[DrawStartingHandsEvent].collect {
      case GameEvent.Decision(MulliganOption.Mulligan, player) => player
    }
    if (playersMulliganing.nonEmpty) {
      playersMulliganing.map(ShuffleHandIntoLibrary) :+ DrawAndMulliganAction(playersMulliganing, mulligansSoFar + 1)
    } else {
      ()
    }
  }
}
