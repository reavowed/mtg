package mtg.cards.text
import mtg.effects.OneShotEffect

case class ModalEffectParagraph(modes: SimpleSpellEffectParagraph*) extends SpellEffectParagraph {
  override def getText(cardName: String): String = ("Choose one —" +: modes.map(_.getText(cardName)).map("• " + _)).mkString("\n")
  override def effects: Seq[OneShotEffect] = ???
}
