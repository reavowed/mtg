package mtg.game.state.history

import mtg.game.PlayerIdentifier

sealed class LogEvent

object LogEvent {
  case class Start(player: PlayerIdentifier) extends LogEvent
  case class Mulligan(player: PlayerIdentifier, newHandSize: Int) extends LogEvent
  case class KeepHand(player: PlayerIdentifier, handSize: Int) extends LogEvent
  case class ReturnCardsToLibrary(player: PlayerIdentifier, numberOfCards: Int) extends LogEvent
  case class NewTurn(player: PlayerIdentifier, turnNumber: Int) extends LogEvent
}