package mtg.game.objects

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}

@JsonSerialize(using = classOf[ObjectId.Serializer])
case class ObjectId(sequentialId: Int) {
  override def toString: String = sequentialId.toString
}

object ObjectId {
  class Serializer extends JsonSerializer[ObjectId] {
    override def serialize(value: ObjectId, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
      gen.writeNumber(value.sequentialId)
    }
  }
}
