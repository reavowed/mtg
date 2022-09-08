package mtg.game

import mtg.cards.CardPrinting
import mtg.definitions.PlayerId

case class PlayerStartingData(playerIdentifier: PlayerId, deck: Seq[CardPrinting], sideboard: Seq[CardPrinting])

