package mtg.game.state

import com.fasterxml.jackson.annotation.JsonIgnore
import mtg.abilities.AbilityWithOrigin.KeywordAbilityWithOrigin
import mtg.abilities._
import mtg.cards.text.{InstructionParagraph, KeywordAbilityParagraph, SingleAbilityTextParagraph, TextParagraph}
import mtg.core.ObjectId
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
  @JsonIgnore derivedTextWithOrigins: Seq[TextParagraphWithAbilityOrigins],
  power: Option[Int],
  toughness: Option[Int],
  loyalty: Option[Int])
{
  def abilitiesWithOrigins: Seq[AbilityWithOrigin] = derivedTextWithOrigins.flatMap(_.abilitiesWithOrigins)
  def abilities: Seq[AbilityDefinition] = abilitiesWithOrigins.map(_.abilityDefinition)
  def instructionParagraphs: Seq[InstructionParagraph] = abilities.ofType[SpellAbility].map(_.instructions)

  def getText(sourceName: String): String = {
    derivedTextWithOrigins.map(_.textParagraph.getText(sourceName)).mkString("\n")
  }

  def addAbility(abilityDefinition: AbilityDefinition): Characteristics = abilityDefinition match {
    case keywordAbility: KeywordAbility =>
      derivedTextWithOrigins.findIndex(_.isInstanceOf[TextParagraphWithAbilityOrigins.KeywordAbilities]) match {
        case Some(index) =>
          copy(derivedTextWithOrigins = derivedTextWithOrigins.updated(
            index,
            derivedTextWithOrigins(index).asInstanceOf[TextParagraphWithAbilityOrigins.KeywordAbilities].add(keywordAbility, AbilityOrigin.Granted)))
        case None =>
          copy(derivedTextWithOrigins = TextParagraphWithAbilityOrigins.KeywordAbilities(Seq(KeywordAbilityWithOrigin(keywordAbility, AbilityOrigin.Granted))) +: derivedTextWithOrigins)
      }
    case abilityParagraph: AbilityParagraph =>
      copy(derivedTextWithOrigins = derivedTextWithOrigins :+ TextParagraphWithAbilityOrigins.TextParagraphWithAbilitySource(abilityParagraph, AbilityOrigin.Granted))
  }
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
    rulesText.map {
      case KeywordAbilityParagraph(abilities) =>
        TextParagraphWithAbilityOrigins.KeywordAbilities(abilities.map(KeywordAbilityWithOrigin(_, AbilityOrigin.Printed)))
      case paragraph: SingleAbilityTextParagraph =>
        TextParagraphWithAbilityOrigins.TextParagraphWithAbilitySource(paragraph, AbilityOrigin.Printed)
    },
    power,
    toughness,
    loyalty)
}
