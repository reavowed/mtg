package mtg.game.start.mulligans

import mtg.events.shuffle.ShuffleHandIntoLibrary
import mtg.game.state.{AutomaticGameAction, GameAction, GameEvent, GameState}

case class ExecuteMulligansAction(mulligansSoFar: Int) extends AutomaticGameAction {
  override def execute(currentGameState: GameState): (GameState, Seq[GameAction]) = {
    val playersMulliganing = currentGameState.gameHistory.preGameEvents.sinceEvent[DrawStartingHandsEvent].collect {
      case GameEvent.Decision(MulliganOption.Mulligan, player) => player
    }
    val remainingMulliganActions = if (playersMulliganing.nonEmpty) {
      playersMulliganing.map(ShuffleHandIntoLibrary) :+ DrawAndMulliganAction(playersMulliganing, mulligansSoFar + 1)
    } else {
      Nil
    }
    (currentGameState, remainingMulliganActions)
  }
}
