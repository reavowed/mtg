package mtg.utils

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}

@JsonSerialize(using = classOf[CaseObject.Serializer])
trait CaseObject {
  def name: String = {
    var name = getClass.getSimpleName
    if (name.endsWith("$")) name = name.substring(0, name.length - 1)
    name
  }
  override def toString: String = name
}

object CaseObject {
  class Serializer extends JsonSerializer[CaseObject] {
    override def serialize(value: CaseObject, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
      gen.writeString(value.name)
    }
  }
}
