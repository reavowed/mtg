package mtg.game.start

import mtg.events.DrawCardsEvent
import mtg.game.PlayerIdentifier
import mtg.game.start.mulligans.DecideMulligansAction
import mtg.game.state.{AutomaticGameAction, GameState}

case class DrawStartingHandsAction(playersToDraw: Seq[PlayerIdentifier], mulligansSoFar: Int) extends AutomaticGameAction {
  def execute(currentGameState: GameState): GameState = {
    currentGameState.runEvents(playersToDraw.map(DrawCardsEvent(_, 7)), DecideMulligansAction(playersToDraw, mulligansSoFar))
  }
}
