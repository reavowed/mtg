package mtg.instructions.joiners

import mtg.core.types.Type
import mtg.core.{ObjectId, ObjectOrPlayerId, PlayerId}
import mtg.effects.{EffectContext, InstructionResolutionContext}
import mtg.game.state.GameState
import mtg.game.state.history.HistoryEvent
import mtg.instructions.adjectives.Adjective
import mtg.instructions.grammar.VerbInflection
import mtg.instructions.nounPhrases.IndefiniteNounPhrase
import mtg.instructions.nouns.ClassNoun
import mtg.instructions.{TransitiveEventMatchingVerb, TypePhrase}
import mtg.utils.TextUtils._

case object Or {
  def apply(types: Type*): TypePhrase = TypePhrase(types: _*)
  def apply[SubjectType <: ObjectOrPlayerId, ObjectType <: ObjectOrPlayerId](
    verbs: TransitiveEventMatchingVerb[SubjectType, ObjectType]*
  ): TransitiveEventMatchingVerb[SubjectType, ObjectType] = new TransitiveEventMatchingVerb[SubjectType, ObjectType] {
    override def inflect(verbInflection: VerbInflection, cardName: String): String = verbs.map(_.inflect(verbInflection, cardName)).toCommaList("or")
    override def matchEvent(
      eventToMatch: HistoryEvent.ResolvedAction[_],
      gameState: GameState,
      effectContext: InstructionResolutionContext,
      playerPhrase: IndefiniteNounPhrase[SubjectType],
      objectPhrase: IndefiniteNounPhrase[ObjectType]
    ): Option[InstructionResolutionContext] = {
      verbs.foldLeft[Option[InstructionResolutionContext]](None)((verb, contextOption) => verb.orElse(contextOption.matchEvent(eventToMatch, gameState, effectContext, playerPhrase, objectPhrase)))
    }
  }
  def apply(adjectives: Adjective*): Adjective = new Adjective {
    override def getText(cardName: String): String = adjectives.map(_.getText(cardName)).toCommaList("or")
    override def describes(objectId: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
      adjectives.exists(_.describes(objectId, gameState, effectContext))
    }
  }
  def apply[T](nouns: ClassNoun[T]*): ClassNoun[T] = new ClassNoun[T] {
    override def getSingular(cardName: String): String = nouns.map(_.getSingular(cardName)).toCommaList("or")
    override def getPlural(cardName: String): String = nouns.map(_.getPlural(cardName)).toCommaList("or")
    override def getAll(gameState: GameState, effectContext: EffectContext): Seq[T] = {
      nouns.flatMap(_.getAll(gameState, effectContext)).distinct
    }
  }
}
