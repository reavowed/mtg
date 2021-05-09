package mtg.game.zone

import mtg.game.PlayerIdentifier
import mtg.game.`object`.GameObject

case class ZoneStates(libraries: Map[PlayerIdentifier, LibraryState])

sealed class ZoneState
case class LibraryState(cards: Seq[GameObject]) extends ZoneState
