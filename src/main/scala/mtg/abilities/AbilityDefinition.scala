package mtg.abilities

import mtg.effects.Effect
import mtg.game.ZoneType
import mtg.parts.costs.Cost

sealed abstract class AbilityDefinition {
  def functionalZones: Set[ZoneType] = Set(ZoneType.Battlefield)
  def text: String
}
object AbilityDefinition {
  implicit def effectToSpellAbility(effect: Effect): SpellAbility = SpellAbility(AbilityParagraph(Seq(effect)))
  implicit def sentenceToSpellAbility(abilitySentence: AbilitySentence): SpellAbility = SpellAbility(AbilityParagraph(Seq(abilitySentence)))
}

case class ActivatedAbilityDefinition(
    costs: Seq[Cost],
    effectParagraph: AbilityParagraph)
  extends AbilityDefinition
{
  override def text: String = costs.map(_.text).mkString(", ") + ": " + effectParagraph.text
}

case class SpellAbility(effectParagraph: AbilityParagraph) extends AbilityDefinition {
  override def text: String = effectParagraph.text
  def effects: Seq[Effect] = effectParagraph.effects
}
