package mtg.cards

import mtg.cards.text.TextParagraph
import mtg.definitions.colors.ColorIndicator
import mtg.definitions.types.{Subtype, Supertype, Type}
import mtg.parts.costs.ManaCost

class CardDefinition(
  val name: String,
  val manaCost: Option[ManaCost],
  val colorIndicator: Option[ColorIndicator],
  val supertypes: Seq[Supertype],
  val types: Seq[Type],
  val subtypes: Seq[Subtype],
  val textParagraphs: Seq[TextParagraph],
  val powerAndToughness: Option[PowerAndToughness],
  val loyalty: Option[Int])
{
  override def toString: String = getClass.getSimpleName
  def text: String = textParagraphs.map(_.getText(name)).mkString("\n")
}
