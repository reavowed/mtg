package mtg.game.objects

import mtg.cards.CardPrinting
import mtg.game.PlayerId
import mtg.game.state.Characteristics
import mtg.parts.costs.ManaSymbol

case class Card(owner: PlayerId, printing: CardPrinting) {
  def baseCharacteristics: Characteristics = {
    import printing.cardDefinition._
    Characteristics(
      name,
      manaCost,
      colorIndicator.map(_.colors.toSet)
        .orElse(manaCost.map(_.symbols.toSet[ManaSymbol].flatMap(_.colors)))
        .getOrElse(Set.empty),
      colorIndicator,
      superTypes,
      types,
      subTypes,
      abilitiesFromRulesText,
      powerAndToughness.map(_.basePower),
      powerAndToughness.map(_.baseToughness),
      loyalty)
  }
}
