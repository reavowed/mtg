package mtg.game

import mtg.cards.CardPrinting

case class PlayerStartingData(playerIdentifier: PlayerIdentifier, deck: Seq[CardPrinting], sideboard: Seq[CardPrinting])

