package net.reavowed.utils

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonSerialize(using = classOf[ValueWrapper.Serializer])
abstract class ValueWrapper[T] {
  def value: T
  override val toString: String = value.toString
}
object ValueWrapper {
  class Serializer extends JsonSerializer[ValueWrapper[_]] {
    override def serialize(value: ValueWrapper[_], gen: JsonGenerator, serializers: SerializerProvider): Unit = {
      gen.writeObject(value.value)
    }
  }
}
