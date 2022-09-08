package mtg.abilities

import mtg.definitions.{ObjectId, PlayerId}

case class ManaAbility(sourceId: ObjectId, controllingPlayer: PlayerId)
