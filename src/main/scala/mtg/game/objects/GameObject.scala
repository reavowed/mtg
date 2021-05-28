package mtg.game.objects

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}
import mtg.game.state.{Characteristics, GameState, PermanentStatus}
import mtg.game.{PlayerIdentifier, Zone}

@JsonSerialize(using = classOf[GameObject.Serializer])
abstract class GameObject {
  def objectId: ObjectId
  def owner: PlayerIdentifier
  def zone: Zone
  def baseCharacteristics: Characteristics
  def defaultController: Option[PlayerIdentifier]
  def permanentStatus: Option[PermanentStatus]
  def markedDamage: Int

  def forNewZone(newObjectId: ObjectId, newZone: Zone, newController: Option[PlayerIdentifier]): GameObject
  def setObjectId(newObjectId: ObjectId): GameObject
  def setZone(newZone: Zone): GameObject
  def setPermanentStatus(newPermanentStatus: Option[PermanentStatus]): GameObject
  def setDefaultController(newDefaultController: Option[PlayerIdentifier]): GameObject
  def updatePermanentStatus(f: PermanentStatus => PermanentStatus): GameObject = setPermanentStatus(permanentStatus.map(f))
  def updateMarkedDamage(f: Int => Int): GameObject

  def currentCharacteristics(gameState: GameState): Characteristics = gameState.derivedState.objectStates(objectId).characteristics
}

object GameObject {
  class Serializer extends JsonSerializer[GameObject] {
    override def serialize(value: GameObject, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
      gen.writeNumber(value.objectId.sequentialId)
    }
  }
}
