package mtg.cards.text

import mtg.abilities.{AbilityDefinition, KeywordAbility}
import mtg.effects.EffectContext

trait TextParagraph {
  def getText(cardName: String): String
  def abilityDefinitions: Seq[AbilityDefinition]
}

object TextParagraph {
  implicit def singleParagraphAsSeq[T <: TextParagraph](textParagraph: T): Seq[T] = Seq(textParagraph)
  implicit def singleKeywordAbilityAsSeq(keywordAbility: KeywordAbility): Seq[TextParagraph] = KeywordAbilityParagraph(Seq(keywordAbility))
}
