package mtg.abilities.builder

import mtg.abilities.KeywordAbility
import mtg.cards.text.{KeywordAbilityParagraph, TextParagraph}

trait ParagraphBuilder {
  implicit def singleKeywordAbilityAsParagraph(keywordAbility: KeywordAbility): TextParagraph = KeywordAbilityParagraph(Seq(keywordAbility))
  implicit def singleKeywordAbilityAsParagraphSeq(keywordAbility: KeywordAbility): Seq[TextParagraph] = KeywordAbilityParagraph(Seq(keywordAbility))
}
