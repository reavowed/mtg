package mtg.utils

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}

@JsonSerialize(using = classOf[CaseObjectWithName.Serializer])
trait CaseObjectWithName {
  def name: String = {
    var name = getClass.getSimpleName
    if (name.endsWith("$")) name = name.substring(0, name.length - 1)
    name
  }
  override def toString: String = name
}

object CaseObjectWithName {
  class Serializer extends JsonSerializer[CaseObjectWithName] {
    override def serialize(value: CaseObjectWithName, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
      gen.writeString(value.name)
    }
  }
}
