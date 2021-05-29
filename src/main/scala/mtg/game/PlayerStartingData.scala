package mtg.game

import mtg.cards.CardPrinting

case class PlayerStartingData(playerIdentifier: PlayerId, deck: Seq[CardPrinting], sideboard: Seq[CardPrinting])

