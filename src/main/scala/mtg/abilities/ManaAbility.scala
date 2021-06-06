package mtg.abilities

import mtg.game.{ObjectId, PlayerId}

case class ManaAbility(sourceId: ObjectId, controllingPlayer: PlayerId)
