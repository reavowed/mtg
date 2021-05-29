package mtg.abilities

import mtg.effects.Effect
import mtg.game.ZoneType
import mtg.parts.costs.Cost

sealed abstract class AbilityDefinition {
  def functionalZones: Set[ZoneType] = Set(ZoneType.Battlefield)
  def getText(cardName: String): String
}
object AbilityDefinition {
  implicit def abilityToSeq(abilityDefinition: AbilityDefinition): Seq[AbilityDefinition] = Seq(abilityDefinition)
  implicit def effectToSpellAbility(effect: Effect): SpellAbility = SpellAbility(AbilityParagraph(effect))
  implicit def effectToSpellAbilitySeq(effect: Effect): Seq[AbilityDefinition] = abilityToSeq(effectToSpellAbility(effect))
  implicit def sentenceToSpellAbility(abilitySentence: AbilitySentence): SpellAbility = SpellAbility(AbilityParagraph(abilitySentence))
  implicit def paragraphToSpellAbility(abilityParagraph: AbilityParagraph): SpellAbility = SpellAbility(abilityParagraph)
  implicit def paragraphToSpellAbilitySeq(abilityParagraph: AbilityParagraph): Seq[AbilityDefinition] = abilityToSeq(paragraphToSpellAbility(abilityParagraph))
}

case class ActivatedAbilityDefinition(
    costs: Seq[Cost],
    effectParagraph: AbilityParagraph)
  extends AbilityDefinition
{
  override def getText(cardName: String): String = costs.map(_.text).mkString(", ") + ": " + effectParagraph.getText(cardName)
}

case class SpellAbility(effectParagraph: AbilityParagraph) extends AbilityDefinition {
  override def getText(cardName: String): String = effectParagraph.getText(cardName)
  def effects: Seq[Effect] = effectParagraph.effects
}
