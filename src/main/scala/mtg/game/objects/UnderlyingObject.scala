package mtg.game.objects

import mtg.abilities.ActivatedOrTriggeredAbilityDefinition
import mtg.cards.CardPrinting
import mtg.core.symbols.ManaSymbol
import mtg.game.state.{Characteristics, GameState}
import mtg.game.{ObjectId, PlayerId}

sealed trait UnderlyingObject {
  def baseCharacteristics: Characteristics
  def owner: PlayerId
  def getSourceName(gameState: GameState): String
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
      superTypes,
      types,
      subTypes,
      textParagraphs,
      powerAndToughness.map(_.basePower),
      powerAndToughness.map(_.baseToughness),
      loyalty)
  }
  override def toString: String = s"${baseCharacteristics.name} ${printing.set}-${printing.collectorNumber}"
  override def getSourceName(gameState: GameState): String = printing.cardDefinition.name
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
    Seq(abilityDefinition.effectParagraph),
    None,
    None,
    None)
  override def getSourceName(gameState: GameState): String = gameState.gameObjectState.getCurrentOrLastKnownState(source).gameObject.underlyingObject.getSourceName(gameState)
}
