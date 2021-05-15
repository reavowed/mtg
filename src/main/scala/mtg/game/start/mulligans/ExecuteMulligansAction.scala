package mtg.game.start.mulligans

import mtg.events.shuffle.ShuffleHandIntoLibrary
import mtg.game.start.DrawStartingHandsAction
import mtg.game.state.GameEvent.Decision
import mtg.game.state.{AutomaticGameAction, GameState}

case class ExecuteMulligansAction(mulliganDecisions: Seq[Decision[MulliganOption]], mulligansSoFar: Int) extends AutomaticGameAction {
  override def execute(currentGameState: GameState): GameState = {
    val playersMulliganing = mulliganDecisions.filter(_.chosenOption == MulliganOption.Mulligan).map(_.playerIdentifier)
    currentGameState.runEvents(playersMulliganing.map(ShuffleHandIntoLibrary), DrawStartingHandsAction(playersMulliganing, mulligansSoFar + 1))
  }
}
