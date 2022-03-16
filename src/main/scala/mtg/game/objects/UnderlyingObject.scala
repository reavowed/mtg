package mtg.game.objects

import mtg.abilities.ActivatedOrTriggeredAbilityDefinition
import mtg.cards.CardPrinting
import mtg.core.{ObjectId, PlayerId}
import mtg.core.symbols.ManaSymbol
import mtg.game.state.{Characteristics, GameState}

sealed trait UnderlyingObject {
  def baseCharacteristics: Characteristics
  def owner: PlayerId
}

case class Card(owner: PlayerId, printing: CardPrinting) extends UnderlyingObject {
  def baseCharacteristics: Characteristics = {
    import printing.cardDefinition._
    Characteristics(
      Some(name),
      manaCost,
      colorIndicator.map(_.colors.toSet)
        .orElse(manaCost.map(_.symbols.toSet[ManaSymbol].flatMap(_.colors)))
        .getOrElse(Set.empty),
      colorIndicator,
      supertypes,
      types,
      subtypes,
      textParagraphs,
      powerAndToughness.map(_.basePower),
      powerAndToughness.map(_.baseToughness),
      loyalty)
  }
  override def toString: String = s"${baseCharacteristics.name} ${printing.set}-${printing.collectorNumber}"
}

case class AbilityOnTheStack(
    abilityDefinition: ActivatedOrTriggeredAbilityDefinition,
    source: ObjectId,
    owner: PlayerId)
  extends UnderlyingObject
{
  def baseCharacteristics: Characteristics = Characteristics(
    None,
    None,
    Set.empty,
    None,
    Nil,
    Nil,
    Nil,
    Seq(abilityDefinition.instructions),
    None,
    None,
    None)
}
