package mtg.cards.text

import mtg.abilities.{AbilityDefinition, KeywordAbility}
import mtg.instructions.TextComponent

trait TextParagraph extends TextComponent {
  def abilityDefinitions: Seq[AbilityDefinition]
}

object TextParagraph {
  implicit def singleParagraphAsSeq[T <: TextParagraph](textParagraph: T): Seq[T] = Seq(textParagraph)
  implicit def singleKeywordAbilityAsSeq(keywordAbility: KeywordAbility): Seq[TextParagraph] = KeywordAbilityParagraph(Seq(keywordAbility))
}
