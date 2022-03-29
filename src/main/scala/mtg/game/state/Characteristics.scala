package mtg.game.state

import com.fasterxml.jackson.annotation.JsonIgnore
import mtg.abilities.{AbilityDefinition, SpellAbility}
import mtg.cards.text.{InstructionParagraph, TextParagraph}
import mtg.core.colors.{Color, ColorIndicator}
import mtg.core.types.{Subtype, Supertype, Type}
import mtg.parts.costs.ManaCost

case class Characteristics(
  name: Option[String],
  manaCost: Option[ManaCost],
  colors: Set[Color],
  colorIndicator: Option[ColorIndicator],
  supertypes: Seq[Supertype],
  types: Seq[Type],
  subtypes: Seq[Subtype],
  @JsonIgnore rulesText: Seq[TextParagraph],
  abilities: Seq[AbilityDefinition],
  power: Option[Int],
  toughness: Option[Int],
  loyalty: Option[Int])
{
  def instructionParagraphs: Seq[InstructionParagraph] = rulesText.ofType[SpellAbility].map(_.instructions)
  def getText: String = rulesText.map(_.getText(name.getOrElse("this object"))).mkString("\n")
}

object Characteristics {
  def apply(
    name: Option[String],
    manaCost: Option[ManaCost],
    colors: Set[Color],
    colorIndicator: Option[ColorIndicator],
    supertypes: Seq[Supertype],
    types: Seq[Type],
    subtypes: Seq[Subtype],
    rulesText: Seq[TextParagraph],
    power: Option[Int],
    toughness: Option[Int],
    loyalty: Option[Int]
  ): Characteristics = Characteristics(
    name,
    manaCost,
    colors,
    colorIndicator,
    supertypes,
    types,
    subtypes,
    rulesText,
    rulesText.flatMap(_.abilityDefinitions),
    power,
    toughness,
    loyalty)
}
