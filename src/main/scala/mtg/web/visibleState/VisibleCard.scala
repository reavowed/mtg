package mtg.web.visibleState

import mtg.cards.CardPrinting

case class VisibleCard(name: String, artDetails: ArtDetails, text: String)

object VisibleCard {
  def apply(cardPrinting: CardPrinting): VisibleCard = VisibleCard(cardPrinting.cardDefinition.name, ArtDetails(cardPrinting), cardPrinting.cardDefinition.text)
}
