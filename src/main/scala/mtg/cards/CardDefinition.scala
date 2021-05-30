package mtg.cards

import mtg.cards.text.TextParagraph
import mtg.characteristics.types.{Subtype, Supertype, Type}
import mtg.parts.costs.ManaCost

class CardDefinition(
  val name: String,
  val manaCost: Option[ManaCost],
  val colorIndicator: Option[ColorIndicator],
  val superTypes: Seq[Supertype],
  val types: Seq[Type],
  val subTypes: Seq[Subtype],
  val textParagraphs: Seq[TextParagraph],
  val powerAndToughness: Option[PowerAndToughness],
  val loyalty: Option[Int])
{
  override def toString: String = getClass.getSimpleName
  def text: String = textParagraphs.map(_.getText(name)).mkString("\n")
}
