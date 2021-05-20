package mtg.game.objects

import mtg.game.state.Characteristics
import mtg.game.{PlayerIdentifier, Zone}

case class CardObject(card: Card, objectId: ObjectId, zone: Zone) extends GameObject {
  override def owner: PlayerIdentifier = card.owner
  override def forNewZone(newObjectId: ObjectId, newZone: Zone): GameObject = copy(objectId = newObjectId, zone = newZone)
  override def baseCharacteristics: Characteristics = card.baseCharacteristics
}
