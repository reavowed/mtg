package mtg.game.`object`

import mtg.game.PlayerIdentifier
import mtg.game.zone.Zone

case class CardObject(card: Card, objectId: ObjectId, zone: Zone) extends GameObject {
  override def owner: PlayerIdentifier = card.owner
  override def forNewZone(newObjectId: ObjectId, newZone: Zone): GameObject = copy(objectId = newObjectId, zone = newZone)
}
