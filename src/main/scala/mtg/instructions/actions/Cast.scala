package mtg.instructions.actions

import mtg.core.{ObjectId, PlayerId}
import mtg.effects.EffectContext
import mtg.game.state.{GameState, GameUpdate}
import mtg.instructions.TransitiveEventMatchingVerb
import mtg.instructions.nouns.IndefiniteNounPhrase
import mtg.stack.adding.FinishCasting
import mtg.text.Verb

case object Cast extends Verb.RegularCaseObject with TransitiveEventMatchingVerb {
  override def matchesEvent(eventToMatch: GameUpdate, gameState: GameState, effectContext: EffectContext, playerPhrase: IndefiniteNounPhrase[PlayerId], objectPhrase: IndefiniteNounPhrase[ObjectId]): Boolean = eventToMatch match {
    case FinishCasting(playerId, spellId) if playerPhrase.describes(playerId, gameState, effectContext) && objectPhrase.describes(spellId, gameState, effectContext) =>
      true
    case _ =>
      false
  }
}
