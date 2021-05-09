package mtg.game.`object`

import mtg.cards.CardPrinting
import mtg.game.PlayerIdentifier

case class Card(owner: PlayerIdentifier, printing: CardPrinting)
