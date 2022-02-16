package mtg.game

import mtg.cards.CardPrinting
import mtg.core.PlayerId

case class PlayerStartingData(playerIdentifier: PlayerId, deck: Seq[CardPrinting], sideboard: Seq[CardPrinting])

