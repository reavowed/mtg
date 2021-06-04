package mtg.game.state.history

import mtg.game.PlayerId

sealed class LogEvent

object LogEvent {
  case class Start(player: PlayerId) extends LogEvent
  case class Mulligan(player: PlayerId, newHandSize: Int) extends LogEvent
  case class KeepHand(player: PlayerId, handSize: Int) extends LogEvent
  case class ReturnCardsToLibrary(player: PlayerId, numberOfCards: Int) extends LogEvent
  case class NewTurn(player: PlayerId, turnNumber: Int) extends LogEvent
  case class DrawForTurn(player: PlayerId) extends LogEvent
  case class SkipFirstDrawStep(player: PlayerId) extends LogEvent
  case class PlayedLand(player: PlayerId, landName: String) extends LogEvent
  case class CastSpell(player: PlayerId, spellName: String, targetNames: Seq[String]) extends LogEvent
  case class ResolvePermanent(player: PlayerId, permanentName: String) extends LogEvent
  case class ResolveSpell(player: PlayerId, spellName: String) extends LogEvent
  case class SpellFailedToResolve(spellName: String) extends LogEvent
  case class DeclareAttackers(player: PlayerId, attackerNames: Seq[String]) extends LogEvent
  case class DeclareBlockers(player: PlayerId, blockerAssignments: Map[String, Seq[String]]) extends LogEvent
  case class OrderBlockers(player: PlayerId, attackerName: String, blockerNames: Seq[String]) extends LogEvent
  case class RevealCard(player: PlayerId, cardName: String) extends LogEvent
  case class Scry(player: PlayerId, cardsOnTop: Int, cardsOnBottom: Int) extends LogEvent
}
