package mtg.cards.text
import mtg.abilities.{AbilityDefinition, AbilityParagraph}

case class AbilityWordParagraph(abilityWord: String, paragraph: AbilityParagraph) extends TextParagraph {
  override def getText(cardName: String): String = abilityWord + " — " + paragraph.getText(cardName)
  override def abilityDefinitions: Seq[AbilityDefinition] = Seq(paragraph)
}
