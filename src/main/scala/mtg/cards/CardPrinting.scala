package mtg.cards

case class CardPrinting(
  cardDefinition: CardDefinition,
  set: Set,
  collectorNumber: Int)
{
  def scryfallApiUrl: String = s"https://api.scryfall.com/cards/${set.code.toLowerCase}/$collectorNumber"
  def id: String = s"$set-$collectorNumber"
  override def toString: String = s"${cardDefinition.name} $id"
}
