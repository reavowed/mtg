package mtg.abilities.builder

import mtg.abilities.TriggeredAbilityDefinition
import mtg.cards.text.SpellEffectParagraph
import mtg.effects.condition.ConditionDefinition

trait TriggeredAbilityBuilder {
  def at(conditionDefinition: ConditionDefinition, paragraph: SpellEffectParagraph) = TriggeredAbilityDefinition(conditionDefinition, paragraph)
}
