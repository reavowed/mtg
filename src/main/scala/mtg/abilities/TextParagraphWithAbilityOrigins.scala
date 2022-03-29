package mtg.abilities

import mtg.abilities.AbilityWithOrigin.{AbilityParagraphWithOrigin, KeywordAbilityWithOrigin}
import mtg.cards.text.{KeywordAbilityParagraph, SingleAbilityTextParagraph, TextParagraph}

sealed trait TextParagraphWithAbilityOrigins {
  def abilitiesWithOrigins: Seq[AbilityWithOrigin]
  def textParagraph: TextParagraph
}
object TextParagraphWithAbilityOrigins {
  case class KeywordAbilities(keywordAbilitiesWithOrigins: Seq[KeywordAbilityWithOrigin]) extends TextParagraphWithAbilityOrigins {
    override def abilitiesWithOrigins: Seq[AbilityWithOrigin] = keywordAbilitiesWithOrigins
    override def textParagraph: TextParagraph = KeywordAbilityParagraph(keywordAbilitiesWithOrigins.map(_.abilityDefinition))
    def add(keywordAbility: KeywordAbility, abilityOrigin: AbilityOrigin): TextParagraphWithAbilityOrigins = {
      KeywordAbilities(keywordAbilitiesWithOrigins :+ KeywordAbilityWithOrigin(keywordAbility, abilityOrigin))
    }
  }
  case class TextParagraphWithAbilitySource(textParagraph: SingleAbilityTextParagraph, abilityOrigin: AbilityOrigin) extends TextParagraphWithAbilityOrigins {
    override def abilitiesWithOrigins: Seq[AbilityWithOrigin] = Seq(AbilityParagraphWithOrigin(textParagraph.abilityDefinition, abilityOrigin))
  }
}
