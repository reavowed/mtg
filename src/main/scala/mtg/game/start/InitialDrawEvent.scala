package mtg.game.start

import mtg.events.{DrawCardsEvent, Event}
import mtg.game.GameState

object InitialDrawEvent extends Event {
  override def execute(currentGameState: GameState): Either[GameState, Seq[Event]] = {
    Right(currentGameState.playersInTurnOrder.map(DrawCardsEvent(_, 7)))
  }
}
