package mtg.instructions

import mtg.core.PlayerId
import mtg.effects.EffectContext
import mtg.game.state.{GameState, GameUpdate}
import mtg.instructions.nouns.IndefiniteNounPhrase
import mtg.text.Verb

trait IntransitiveEventMatchingVerb extends Verb {
  def matchesEvent(eventToMatch: GameUpdate, gameState: GameState, effectContext: EffectContext, playerPhrase: IndefiniteNounPhrase[PlayerId]): Boolean
}
