package mtg.cards

case class CardPrinting(
  cardDefinition: CardDefinition,
  set: Set,
  collectorNumber: Int)
{
  def scryfallApiUrl: String = s"https://api.scryfall.com/cards/${set.code.toLowerCase}/$collectorNumber"
}
