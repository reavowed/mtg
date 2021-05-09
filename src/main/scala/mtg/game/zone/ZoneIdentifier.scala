package mtg.game.zone

import mtg.game.PlayerIdentifier

sealed abstract class ZoneIdentifier {
  def getZoneState(zoneStates: ZoneStates): ZoneState
}
class LibraryIdentifier(playerIdentifier: PlayerIdentifier) extends ZoneIdentifier {
  override def getZoneState(zoneStates: ZoneStates): LibraryState = zoneStates.libraries(playerIdentifier)
}
