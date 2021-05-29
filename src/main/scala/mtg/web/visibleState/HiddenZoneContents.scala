package mtg.web.visibleState

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}

@JsonSerialize(using = classOf[HiddenZoneContents.Serializer])
sealed trait HiddenZoneContents

object HiddenZoneContents {
  case class CanSee(contents: Seq[VisibleGameObject]) extends HiddenZoneContents
  case class CantSee(numberOfCards: Int) extends HiddenZoneContents

  class Serializer extends JsonSerializer[HiddenZoneContents] {
    override def serialize(value: HiddenZoneContents, gen: JsonGenerator, serializers: SerializerProvider): Unit = value match {
      case CanSee(contents) => gen.writeObject(contents)
      case CantSee(numberOfCards) => gen.writeNumber(numberOfCards)
    }
  }
}
