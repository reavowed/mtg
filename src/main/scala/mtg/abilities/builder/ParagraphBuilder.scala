package mtg.abilities.builder

import mtg.abilities.{ActivatedAbilityDefinition, KeywordAbility}
import mtg.cards.text._
import mtg.parts.costs.Cost

trait ParagraphBuilder {
  def activatedAbility(costs: Cost*)(effectParagraph: InstructionParagraph): ActivatedAbilityDefinition = {
    ActivatedAbilityDefinition(costs, effectParagraph)
  }
  def chooseOne(modes: SimpleInstructionParagraph*): ModalInstructionParagraph = ModalInstructionParagraph(modes: _*)
}
