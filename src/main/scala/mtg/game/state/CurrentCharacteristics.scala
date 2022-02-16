package mtg.game.state

import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}

object CurrentCharacteristics {
  def getName(objectOrPlayer: ObjectOrPlayer, gameState: GameState): String = objectOrPlayer match {
        case objectId: ObjectId =>
          getName(gameState.gameObjectState.derivedState.allObjectStates(objectId))
        case playerId: PlayerId =>
          playerId.id
  }
  def getName(objectWithState: ObjectWithState): String = objectWithState.characteristics.name.getOrElse("<unnamed object>")
}
