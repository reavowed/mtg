package mtg.game.objects

import mtg.abilities.{ActivatedOrTriggeredAbilityDefinition, SpellAbility}
import mtg.cards.CardPrinting
import mtg.definitions.symbols.ManaSymbol
import mtg.definitions.{ObjectId, ObjectOrPlayerId, PlayerId}
import mtg.game.state.Characteristics

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
  override def toString: String = printing.toString
}

case class CopyOfSpell(baseCharacteristics: Characteristics, owner: PlayerId) extends UnderlyingObject

case class AbilityOnTheStack(
    abilityDefinition: ActivatedOrTriggeredAbilityDefinition,
    source: ObjectId,
    owner: PlayerId,
    identifiedObjects: Seq[ObjectOrPlayerId])
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
    Seq(SpellAbility(abilityDefinition.instructions)),
    None,
    None,
    None)
}
