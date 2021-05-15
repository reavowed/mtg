package mtg.game.start.mulligans

import mtg.events.shuffle.ShuffleHandIntoLibrary
import mtg.game.start.DrawStartingHandsAction
import mtg.game.state.GameEvent.Decision
import mtg.game.state.{AutomaticGameAction, GameAction, GameState, HandleEventsAction}

case class ExecuteMulligansAction(mulliganDecisions: Seq[Decision[MulliganOption]], mulligansSoFar: Int) extends AutomaticGameAction {
  override def execute(currentGameState: GameState): (GameState, GameAction) = {
    val playersMulliganing = mulliganDecisions.filter(_.chosenOption == MulliganOption.Mulligan).map(_.playerIdentifier)
    (currentGameState, HandleEventsAction(playersMulliganing.map(ShuffleHandIntoLibrary), DrawStartingHandsAction(playersMulliganing, mulligansSoFar + 1)))
  }
}
