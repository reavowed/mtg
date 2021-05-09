package mtg.game.zone

import mtg.game.PlayerIdentifier

sealed class Zone

sealed class PlayerSpecificZone(val playerIdentifier: PlayerIdentifier) extends Zone

case class Library(override val playerIdentifier: PlayerIdentifier) extends PlayerSpecificZone(playerIdentifier)
