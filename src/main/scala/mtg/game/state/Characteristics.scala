package mtg.game.state

import com.fasterxml.jackson.annotation.JsonIgnore
import mtg.abilities.AbilityDefinition
import mtg.cards.ColorIndicator
import mtg.cards.text.TextParagraph
import mtg.characteristics.types.{Subtype, Supertype, Type}
import mtg.core.colors.Color
import mtg.parts.costs.ManaCost

case class Characteristics(
  name: Option[String],
  manaCost: Option[ManaCost],
  colors: Set[Color],
  colorIndicator: Option[ColorIndicator],
  superTypes: Seq[Supertype],
  types: Seq[Type],
  subTypes: Seq[Subtype],
  @JsonIgnore rulesText: Seq[TextParagraph],
  abilities: Seq[AbilityDefinition],
  power: Option[Int],
  toughness: Option[Int],
  loyalty: Option[Int])
{
  def getText: String = rulesText.map(_.getText(name.getOrElse("this object"))).mkString("\n")
}

object Characteristics {
  def apply(
    name: Option[String],
    manaCost: Option[ManaCost],
    colors: Set[Color],
    colorIndicator: Option[ColorIndicator],
    superTypes: Seq[Supertype],
    types: Seq[Type],
    subTypes: Seq[Subtype],
    rulesText: Seq[TextParagraph],
    power: Option[Int],
    toughness: Option[Int],
    loyalty: Option[Int]
  ): Characteristics = Characteristics(
    name,
    manaCost,
    colors,
    colorIndicator,
    superTypes,
    types,
    subTypes,
    rulesText,
    rulesText.flatMap(_.abilityDefinitions),
    power,
    toughness,
    loyalty)
}
