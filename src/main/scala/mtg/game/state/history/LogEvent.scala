package mtg.game.state.history

import mtg.game.PlayerIdentifier

sealed class LogEvent

object LogEvent {
  case class Start(player: PlayerIdentifier) extends LogEvent
  case class Mulligan(player: PlayerIdentifier, newHandSize: Int) extends LogEvent
  case class KeepHand(player: PlayerIdentifier, handSize: Int) extends LogEvent
  case class ReturnCardsToLibrary(player: PlayerIdentifier, numberOfCards: Int) extends LogEvent
  case class NewTurn(player: PlayerIdentifier, turnNumber: Int) extends LogEvent
  case class DrawForTurn(player: PlayerIdentifier) extends LogEvent
  case class SkipFirstDrawStep(player: PlayerIdentifier) extends LogEvent
  case class PlayedLand(player: PlayerIdentifier, landName: String) extends LogEvent
  case class CastSpell(player: PlayerIdentifier, spellName: String) extends LogEvent
  case class ResolvePermanent(player: PlayerIdentifier, permanentName: String) extends LogEvent
  case class DeclareAttackers(player: PlayerIdentifier, attackerNames: Seq[String]) extends LogEvent
  case class DeclareBlockers(player: PlayerIdentifier, blockerAssignments: Map[String, Seq[String]]) extends LogEvent
  case class OrderBlockers(player: PlayerIdentifier, attackerName: String, blockerNames: Seq[String]) extends LogEvent
  case class RevealCard(player: PlayerIdentifier, cardName: String) extends LogEvent
}
