package mtg.instructions.verbs

import mtg.core.{ObjectId, PlayerId}
import mtg.effects.EffectContext
import mtg.game.state.{GameAction, GameState}
import mtg.instructions.nounPhrases.IndefiniteNounPhrase
import mtg.instructions.{TransitiveEventMatchingVerb, Verb}
import mtg.stack.adding.FinishCasting

case object Cast extends Verb.RegularCaseObject with TransitiveEventMatchingVerb {
  override def matchesEvent(eventToMatch: GameAction[_], gameState: GameState, effectContext: EffectContext, playerPhrase: IndefiniteNounPhrase[PlayerId], objectPhrase: IndefiniteNounPhrase[ObjectId]): Boolean = eventToMatch match {
    case FinishCasting(playerId, spellId) if playerPhrase.describes(playerId, gameState, effectContext) && objectPhrase.describes(spellId, gameState, effectContext) =>
      true
    case _ =>
      false
  }
}
