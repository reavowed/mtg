package mtg.abilities.builder

import mtg.abilities.TriggeredAbilityDefinition
import mtg.cards.text.InstructionParagraph
import mtg.effects.condition.ConditionDefinition

trait TriggeredAbilityBuilder {
  def at(conditionDefinition: ConditionDefinition, paragraph: InstructionParagraph) = TriggeredAbilityDefinition(conditionDefinition, paragraph)
}
