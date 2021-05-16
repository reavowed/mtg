package mtg.game

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonSerialize(using = classOf[PlayerIdentifier.Serializer])
case class PlayerIdentifier(id: String) {
  override def toString: String = id
}
object PlayerIdentifier {
  class Serializer extends JsonSerializer[PlayerIdentifier] {
    override def serialize(value: PlayerIdentifier, gen: JsonGenerator, serializers: SerializerProvider): Unit = gen.writeString(value.id)
  }
}
