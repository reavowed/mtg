package mtg.game.start

import mtg.events.DrawCardsEvent
import mtg.game.PlayerIdentifier
import mtg.game.start.mulligans.DecideMulligansAction
import mtg.game.state.{AutomaticGameAction, GameAction, GameState, HandleEventsAction}

case class DrawStartingHandsAction(playersToDraw: Seq[PlayerIdentifier], mulligansSoFar: Int) extends AutomaticGameAction {
  def execute(currentGameState: GameState): (GameState, GameAction) = {
    (currentGameState, HandleEventsAction(playersToDraw.map(DrawCardsEvent(_, 7)), DecideMulligansAction(playersToDraw, mulligansSoFar)))
  }
}
