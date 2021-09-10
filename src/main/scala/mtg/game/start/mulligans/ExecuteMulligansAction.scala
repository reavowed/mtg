package mtg.game.start.mulligans

import mtg.events.shuffle.ShuffleHandIntoLibrary
import mtg.game.state.history.{GameEvent, LogEvent}
import mtg.game.state.{GameAction, GameState, InternalGameAction, GameActionResult}

case class ExecuteMulligansAction(mulligansSoFar: Int) extends InternalGameAction {
  override def execute(currentGameState: GameState): GameActionResult = {
    val playersWhoHaveTakenMulligans = currentGameState.eventsSinceEvent[DrawStartingHandsAction].collect {
      case GameEvent.Decision(MulliganOption.Mulligan, player) => player
    }.toSet
    val playersToDraw = currentGameState.gameData.playersInTurnOrder.filter(playersWhoHaveTakenMulligans.contains)
    if (playersWhoHaveTakenMulligans.nonEmpty) {
      playersToDraw.map(ShuffleHandIntoLibrary) :+ DrawAndMulliganAction(playersToDraw, mulligansSoFar + 1)
    } else {
      ()
    }
  }
}
