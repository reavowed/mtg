package mtg.game.objects

import mtg.game.PlayerIdentifier
import mtg.game.zone.Zone

abstract class GameObject {
  def objectId: ObjectId
  def owner: PlayerIdentifier
  def zone: Zone

  def forNewZone(newObjectId: ObjectId, newZone: Zone): GameObject
}
