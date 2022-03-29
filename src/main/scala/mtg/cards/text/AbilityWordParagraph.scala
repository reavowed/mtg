package mtg.cards.text
import mtg.abilities.AbilityParagraph

case class AbilityWordParagraph(abilityWord: String, innerAbilityParagraph: AbilityParagraph) extends SingleAbilityTextParagraph {
  override def getText(cardName: String): String = abilityWord + " — " + innerAbilityParagraph.getText(cardName)
  override def abilityDefinition: AbilityParagraph = innerAbilityParagraph
}
