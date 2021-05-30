package mtg.abilities

import mtg.effects.oneshot.OneShotEffect
import mtg.game.ZoneType
import mtg.parts.costs.Cost

sealed abstract class AbilityDefinition {
  def functionalZones: Set[ZoneType] = Set(ZoneType.Battlefield)
  def getText(cardName: String): String
}
object AbilityDefinition {
  implicit def abilityToSeq(abilityDefinition: AbilityDefinition): Seq[AbilityDefinition] = Seq(abilityDefinition)
  implicit def effectToSpellAbility(effect: OneShotEffect): SpellAbility = SpellAbility(EffectParagraph(effect))
  implicit def effectToSpellAbilitySeq(effect: OneShotEffect): Seq[AbilityDefinition] = abilityToSeq(effectToSpellAbility(effect))
  implicit def sentenceToSpellAbility(sentence: EffectSentence): SpellAbility = SpellAbility(EffectParagraph(sentence))
  implicit def paragraphToSpellAbility(paragraph: EffectParagraph): SpellAbility = SpellAbility(paragraph)
  implicit def paragraphToSpellAbilitySeq(paragraph: EffectParagraph): Seq[AbilityDefinition] = abilityToSeq(paragraphToSpellAbility(paragraph))
}

case class ActivatedAbilityDefinition(
    costs: Seq[Cost],
    effectParagraph: EffectParagraph)
  extends AbilityDefinition
{
  override def getText(cardName: String): String = costs.map(_.text).mkString(", ") + ": " + effectParagraph.getText(cardName)
}

case class SpellAbility(effectParagraph: EffectParagraph) extends AbilityDefinition {
  override def getText(cardName: String): String = effectParagraph.getText(cardName)
  def effects: Seq[OneShotEffect] = effectParagraph.effects
}
