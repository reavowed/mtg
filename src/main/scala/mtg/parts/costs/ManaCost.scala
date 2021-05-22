package mtg.parts.costs

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}

@JsonSerialize(using = classOf[ManaCost.Serializer])
case class ManaCost(symbols: ManaSymbol*)

object ManaCost {
  class Serializer extends JsonSerializer[ManaCost] {
    override def serialize(value: ManaCost, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
      gen.writeString(value.symbols.map(_.text).mkString)
    }
  }
}
