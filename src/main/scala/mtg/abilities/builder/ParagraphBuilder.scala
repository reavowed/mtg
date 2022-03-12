package mtg.abilities.builder

import mtg.abilities.{ActivatedAbilityDefinition, KeywordAbility}
import mtg.cards.text._
import mtg.parts.costs.Cost

trait ParagraphBuilder {
  def activatedAbility(costs: Cost*)(effectParagraph: InstructionParagraph): ActivatedAbilityDefinition = {
    ActivatedAbilityDefinition(costs, effectParagraph)
  }
  def chooseOne(modes: SimpleInstructionParagraph*): ModalInstructionParagraph = ModalInstructionParagraph(modes: _*)

  implicit def singleKeywordAbilityAsParagraph(keywordAbility: KeywordAbility): TextParagraph = KeywordAbilityParagraph(Seq(keywordAbility))
  implicit def singleKeywordAbilityAsParagraphSeq(keywordAbility: KeywordAbility): Seq[TextParagraph] = KeywordAbilityParagraph(Seq(keywordAbility))
}
