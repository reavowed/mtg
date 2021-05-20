package mtg.game.objects

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}
import mtg.game.state.Characteristics
import mtg.game.{PlayerIdentifier, Zone}

@JsonSerialize(using = classOf[GameObject.Serializer])
abstract class GameObject {
  def objectId: ObjectId
  def owner: PlayerIdentifier
  def zone: Zone
  def baseCharacteristics: Characteristics

  def forNewZone(newObjectId: ObjectId, newZone: Zone): GameObject
}

object GameObject {
  class Serializer extends JsonSerializer[GameObject] {
    override def serialize(value: GameObject, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
      gen.writeNumber(value.objectId.sequentialId)
    }
  }
}
