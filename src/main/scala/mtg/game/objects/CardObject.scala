package mtg.game.objects

import mtg.game.state.{Characteristics, PermanentStatus}
import mtg.game.{PlayerIdentifier, Zone}

case class CardObject(card: Card, objectId: ObjectId, zone: Zone, permanentStatus: Option[PermanentStatus]) extends GameObject {
  override def owner: PlayerIdentifier = card.owner
  override def forNewZone(newObjectId: ObjectId, newZone: Zone): GameObject = copy(objectId = newObjectId, zone = newZone)
  override def baseCharacteristics: Characteristics = card.baseCharacteristics

  override def setObjectId(newObjectId: ObjectId): GameObject = copy(objectId = newObjectId)
  override def setZone(newZone: Zone): GameObject = copy(zone = newZone)
  override def setPermanentStatus(newPermanentStatus: Option[PermanentStatus]): GameObject = copy(permanentStatus = newPermanentStatus)
}
