package mtg.game.loop

import mtg.events.Event
import mtg.game.GameState

case class ResolveEventAction(event: Event, gameState: GameState, getNext: GameState => Either[Action, Either[Choice, GameResult]]) extends Action {
  override def execute(): Either[Action, Either[Choice, GameResult]] = {
    val newGameState = GameLoop.resolveEvent(event, gameState)
    getNext(newGameState)
  }
}
