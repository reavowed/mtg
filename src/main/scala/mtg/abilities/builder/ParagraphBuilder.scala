package mtg.abilities.builder

import mtg.abilities.KeywordAbility
import mtg.cards.text.{KeywordAbilityParagraph, ModalEffectParagraph, SimpleSpellEffectParagraph, TextParagraph}

trait ParagraphBuilder {
  def chooseOne(modes: SimpleSpellEffectParagraph*): ModalEffectParagraph = ModalEffectParagraph(modes: _*)

  implicit def singleKeywordAbilityAsParagraph(keywordAbility: KeywordAbility): TextParagraph = KeywordAbilityParagraph(Seq(keywordAbility))
  implicit def singleKeywordAbilityAsParagraphSeq(keywordAbility: KeywordAbility): Seq[TextParagraph] = KeywordAbilityParagraph(Seq(keywordAbility))
}
