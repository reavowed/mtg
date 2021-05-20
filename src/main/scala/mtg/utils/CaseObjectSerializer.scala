package mtg.utils

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}

class CaseObjectSerializer extends JsonSerializer[AnyRef] {
  override def serialize(value: AnyRef, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
    var name = value.getClass.getSimpleName
    if (name.endsWith("$")) name = name.substring(0, name.length - 1)
    gen.writeString(name)
  }
}
