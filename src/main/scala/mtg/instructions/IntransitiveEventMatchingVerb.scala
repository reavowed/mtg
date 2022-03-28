package mtg.instructions

import mtg.core.PlayerId
import mtg.effects.EffectContext
import mtg.effects.condition.Condition
import mtg.game.state.{GameState, GameUpdate}
import mtg.instructions.nouns.{IndefiniteNounPhrase, SingleIdentifyingNounPhrase}
import mtg.text.{Verb, VerbInflection}

trait IntransitiveEventMatchingVerb extends Verb {
  def matchesEvent(eventToMatch: GameUpdate, gameState: GameState, effectContext: EffectContext, playerPhrase: IndefiniteNounPhrase[PlayerId]): Boolean
}

object IntransitiveEventMatchingVerb {
  case class WithSubject(playerPhrase: IndefiniteNounPhrase[PlayerId], verb: IntransitiveEventMatchingVerb) extends Condition {
    override def matchesEvent(eventToMatch: GameUpdate, gameState: GameState, effectContext: EffectContext): Boolean = {
      verb.matchesEvent(eventToMatch, gameState, effectContext, playerPhrase)
    }
    override def getText(cardName: String): String = {
      playerPhrase.getText(cardName) + " " + verb.inflect(VerbInflection.Present(playerPhrase.person, playerPhrase.number), cardName)
    }
  }
}
