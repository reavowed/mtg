package mtg.cards.text
import mtg.abilities.AbilityDefinition

case class AbilityWordParagraph(abilityWord: String, paragraph: TextParagraph) extends TextParagraph {
  override def getText(cardName: String): String = abilityWord + " — " + paragraph.getText(cardName)
  override def abilityDefinitions: Seq[AbilityDefinition] = paragraph.abilityDefinitions
}
