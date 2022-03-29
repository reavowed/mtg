package mtg.instructions.joiners

import mtg.core.{ObjectId, PlayerId}
import mtg.effects.EffectContext
import mtg.game.state.{GameState, GameUpdate}
import mtg.instructions.TransitiveEventMatchingVerb
import mtg.instructions.adjectives.Adjective
import mtg.instructions.nounPhrases.IndefiniteNounPhrase
import mtg.text.VerbInflection
import mtg.utils.TextUtils._

case object Or {
  def apply(verbs: TransitiveEventMatchingVerb*): TransitiveEventMatchingVerb = new TransitiveEventMatchingVerb {
    override def inflect(verbInflection: VerbInflection, cardName: String): String = verbs.map(_.inflect(verbInflection, cardName)).toCommaList("or")
    override def matchesEvent(
      eventToMatch: GameUpdate,
      gameState: GameState,
      effectContext: EffectContext,
      playerPhrase: IndefiniteNounPhrase[PlayerId],
      objectPhrase: IndefiniteNounPhrase[ObjectId]
    ): Boolean = {
      verbs.exists(_.matchesEvent(eventToMatch, gameState, effectContext, playerPhrase, objectPhrase))
    }
  }
  def apply[T](adjectives: Adjective*): Adjective = new Adjective {
    override def getText(cardName: String): String = adjectives.map(_.getText(cardName)).toCommaList("or")
    override def describes(objectId: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
      adjectives.exists(_.describes(objectId, gameState, effectContext))
    }
  }
}
