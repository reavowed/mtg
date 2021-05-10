package mtg.game.zone

import mtg.game.PlayerIdentifier
import mtg.game.`object`.GameObject

case class ZoneStates(libraries: Map[PlayerIdentifier, ZoneState], hands: Map[PlayerIdentifier, ZoneState])
case class ZoneState(objects: Seq[GameObject])
