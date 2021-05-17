package mtg.game.start.mulligans

import mtg.events.shuffle.ShuffleHandIntoLibrary
import mtg.game.state.history.{GameEvent, LogEvent}
import mtg.game.state.{GameAction, GameState, InternalGameAction}

case class ExecuteMulligansAction(mulligansSoFar: Int) extends InternalGameAction {
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    val playersMulliganing = currentGameState.gameHistory.preGameEvents.sinceEvent[DrawStartingHandsEvent].collect {
      case GameEvent.Decision(MulliganOption.Mulligan, player) => player
    }
    val remainingMulliganActions = if (playersMulliganing.nonEmpty) {
      playersMulliganing.map(ShuffleHandIntoLibrary) :+ DrawAndMulliganAction(playersMulliganing, mulligansSoFar + 1)
    } else {
      Nil
    }
    (remainingMulliganActions, None)
  }
}
