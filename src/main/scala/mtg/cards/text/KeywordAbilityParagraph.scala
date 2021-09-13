package mtg.cards.text

import mtg.abilities.{AbilityDefinition, KeywordAbility}

case class KeywordAbilityParagraph(abilities: Seq[KeywordAbility]) extends TextParagraph {
  override def getText(cardName: String): String = (abilities.head.getText(cardName).capitalize +: abilities.tail.map(_.getText(cardName))).mkString(", ")
  override def abilityDefinitions: Seq[AbilityDefinition] = abilities
}
