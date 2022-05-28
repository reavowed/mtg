package mtg.instructions

import mtg.core.{ObjectId, PlayerId}
import mtg.effects.EffectContext
import mtg.game.state.GameState
import mtg.game.state.history.HistoryEvent
import mtg.instructions.grammar.VerbInflection
import mtg.instructions.nounPhrases.IndefiniteNounPhrase

trait TransitiveEventMatchingVerb extends Verb {
  def matchesEvent(eventToMatch: HistoryEvent.ResolvedAction[_], gameState: GameState, effectContext: EffectContext, playerPhrase: IndefiniteNounPhrase[PlayerId], objectPhrase: IndefiniteNounPhrase[ObjectId]): Boolean
  def apply(objectPhrase: IndefiniteNounPhrase[ObjectId]): IntransitiveEventMatchingVerb[PlayerId] = {
    TransitiveEventMatchingVerbWithObject(this, objectPhrase)
  }
}

case class TransitiveEventMatchingVerbWithObject(
    transitiveEventMatchingVerb: TransitiveEventMatchingVerb,
    objectPhrase: IndefiniteNounPhrase[ObjectId])
  extends IntransitiveEventMatchingVerb[PlayerId]
{
  override def inflect(verbInflection: VerbInflection, cardName: String): String = transitiveEventMatchingVerb.inflect(verbInflection, cardName) + " " + objectPhrase.getText(cardName)
  override def matchesEvent(eventToMatch: HistoryEvent.ResolvedAction[_], gameState: GameState, effectContext: EffectContext, playerPhrase: IndefiniteNounPhrase[PlayerId]): Boolean = {
    transitiveEventMatchingVerb.matchesEvent(eventToMatch, gameState, effectContext, playerPhrase, objectPhrase)
  }
}

