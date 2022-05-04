package mtg.game.state

import mtg.core.{ObjectId, ObjectOrPlayerId, PlayerId}
import mtg.game.objects.{PermanentObject, StackObject}

object CurrentCharacteristics {
  def getState(objectId: ObjectId, gameState: GameState): ObjectWithState = {
    gameState.gameObjectState.derivedState.allObjectStates(objectId)
  }
  def getCharacteristics(objectId: ObjectId, gameState: GameState): Characteristics = {
    getState(objectId, gameState).characteristics
  }
  def getLastKnownState(objectId: ObjectId, gameState: GameState): ObjectWithState = {
    gameState.gameObjectState.getCurrentOrLastKnownState(objectId)
  }
  def getLastKnownCharacteristics(objectId: ObjectId, gameState: GameState): Characteristics = {
    getLastKnownState(objectId, gameState).characteristics
  }
  def getPermanentObject(objectId: ObjectId, gameState: GameState): Option[PermanentObject] = gameState.gameObjectState.battlefield.find(_.objectId == objectId)
  def getStackObject(objectId: ObjectId, gameState: GameState): Option[StackObject] = gameState.gameObjectState.stack.find(_.objectId == objectId)

  def getName(objectOrPlayer: ObjectOrPlayerId, gameState: GameState): String = objectOrPlayer match {
        case objectId: ObjectId =>
          getName(getLastKnownCharacteristics(objectId, gameState))
        case playerId: PlayerId =>
          playerId.toString
  }
  def getName(objectWithState: ObjectWithState): String = getName(objectWithState.characteristics)
  private def getName(characteristics: Characteristics): String = {
    characteristics.name.getOrElse("<unnamed object>")
  }

  def getPower(objectId: ObjectId, gameState: GameState): Int = getCharacteristics(objectId, gameState).power.getOrElse(0)
  def getToughness(objectId: ObjectId, gameState: GameState): Int = getCharacteristics(objectId, gameState).toughness.getOrElse(0)
  def getMarkedDamage(objectId: ObjectId, gameState: GameState): Int = getPermanentObject(objectId, gameState).get.markedDamage
}
