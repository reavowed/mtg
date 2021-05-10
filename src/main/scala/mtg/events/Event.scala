package mtg.events

import mtg.game.GameState

abstract class Event {
  def execute(currentGameState: GameState): Either[GameState, Seq[Event]]
}
