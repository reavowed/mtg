package mtg.game.objects

import mtg.game.state.{Characteristics, PermanentStatus}
import mtg.game.{PlayerIdentifier, Zone}

case class CardObject(card: Card, objectId: ObjectId, zone: Zone, defaultController: Option[PlayerIdentifier], permanentStatus: Option[PermanentStatus], markedDamage: Int) extends GameObject {
  override def owner: PlayerIdentifier = card.owner
  override def forNewZone(newObjectId: ObjectId, newZone: Zone): GameObject = copy(objectId = newObjectId, zone = newZone)
  override def baseCharacteristics: Characteristics = card.baseCharacteristics

  override def setObjectId(newObjectId: ObjectId): CardObject = copy(objectId = newObjectId)
  override def setZone(newZone: Zone): CardObject = copy(zone = newZone)
  override def setDefaultController(newDefaultController: Option[PlayerIdentifier]): CardObject = copy(defaultController = newDefaultController)
  override def setPermanentStatus(newPermanentStatus: Option[PermanentStatus]): CardObject = copy(permanentStatus = newPermanentStatus)
  override def updateMarkedDamage(f: Int => Int): CardObject = copy(markedDamage = f(markedDamage))

  override def toString: String = s"CardObject(${card.baseCharacteristics.name} ${card.printing.set}-${card.printing.collectorNumber}, $objectId)"
}
