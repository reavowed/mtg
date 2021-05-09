package mtg.game.`object`

import mtg.game.zone.{Zone, ZoneIdentifier}

class CardObject(val card: Card, objectId: ObjectId, zoneIdentifier: ZoneIdentifier)
    extends GameObject(objectId, zoneIdentifier)
