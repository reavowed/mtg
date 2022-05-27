package mtg.instructions

import mtg.core.ObjectId
import mtg.core.types.Type
import mtg.effects.EffectContext
import mtg.effects.condition.Condition
import mtg.game.state.{Characteristics, GameState}
import mtg.instructions.adjectives.Adjective
import mtg.instructions.nounPhrases.PluralNoun
import mtg.instructions.nouns.{ClassNoun, Permanent}
import mtg.utils.TextUtils._

case class TypePhrase(types: Type*) extends ClassNoun[ObjectId] with Adjective with Descriptor.CharacteristicDescriptor {
  override def getText(cardName: String): String = types.map(_.name.toLowerCase).toCommaList("or")
  override def getSingular(cardName: String): String = getText(cardName)
  override def getAll(gameState: GameState, effectContext: EffectContext): Seq[ObjectId] = {
    apply(Permanent).getAll(gameState, effectContext)
  }
  override def describes(characteristics: Characteristics, gameState: GameState, effectContext: EffectContext): Boolean = {
    types.exists(characteristics.types.contains)
  }

  def apply(verb: CharacteristicChangingVerb, endCondition: Condition): Instruction = {
    CharacteristicChangingVerb.WithSubjectAndCondition(PluralNoun(this), verb, endCondition)
  }
  def apply(suffixDescriptor: SuffixDescriptor): ClassNoun[ObjectId] = ClassNoun.WithSuffix(this, suffixDescriptor)
}
