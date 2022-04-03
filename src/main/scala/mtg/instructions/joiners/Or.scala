package mtg.instructions.joiners

import mtg.core.types.Type
import mtg.core.{ObjectId, ObjectOrPlayerId, PlayerId}
import mtg.effects.EffectContext
import mtg.game.state.{GameAction, GameState}
import mtg.instructions.adjectives.Adjective
import mtg.instructions.nounPhrases.IndefiniteNounPhrase
import mtg.instructions.nouns.Noun
import mtg.instructions.{TransitiveEventMatchingVerb, TypePhrase, VerbInflection}
import mtg.utils.TextUtils._

case object Or {
  def apply(types: Type*): TypePhrase = TypePhrase(types: _*)
  def apply(verbs: TransitiveEventMatchingVerb*): TransitiveEventMatchingVerb = new TransitiveEventMatchingVerb {
    override def inflect(verbInflection: VerbInflection, cardName: String): String = verbs.map(_.inflect(verbInflection, cardName)).toCommaList("or")
    override def matchesEvent(
      eventToMatch: GameAction[_],
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
  def apply[T <: ObjectOrPlayerId](nouns: Noun[T]*): Noun[T] = new Noun[T] {
    override def getSingular(cardName: String): String = nouns.map(_.getSingular(cardName)).toCommaList("or")
    override def getPlural(cardName: String): String = nouns.map(_.getPlural(cardName)).toCommaList("or")
    override def getAll(gameState: GameState, effectContext: EffectContext): Seq[T] = {
      nouns.flatMap(_.getAll(gameState, effectContext)).distinct
    }
  }
}
