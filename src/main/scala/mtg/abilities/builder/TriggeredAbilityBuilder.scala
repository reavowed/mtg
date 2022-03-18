package mtg.abilities.builder

import mtg.abilities.TriggeredAbilityDefinition
import mtg.cards.text.InstructionParagraph
import mtg.effects.condition.Condition

trait TriggeredAbilityBuilder {
  def at(condition: Condition, paragraph: InstructionParagraph) = TriggeredAbilityDefinition(condition, paragraph)
}
