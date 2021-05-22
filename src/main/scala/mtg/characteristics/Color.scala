package mtg.characteristics

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}
import mtg.parts.mana.{ColoredMana, ColorlessMana, ManaType}

sealed abstract class ColorOrColorless(val letter: String) {
  def manaType: ManaType
}

object ColorOrColorless {
  final val All = Color.All :+ Colorless
}

@JsonSerialize(using = classOf[Color.Serializer])
sealed class Color(letter: String) extends ColorOrColorless(letter) {
  override def manaType: ManaType = ColoredMana.ByColor(this)
}
object Color {
  case object White extends Color("W")
  case object Blue extends Color("U")
  case object Black extends Color("B")
  case object Red extends Color("R")
  case object Green extends Color("G")
  final val All = Seq(White, Blue, Black, Red, Green)

  class Serializer extends JsonSerializer[Color] {
    override def serialize(value: Color, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
      gen.writeString(value.letter)
    }
  }
}

case object Colorless extends ColorOrColorless("C") {
  override def manaType: ManaType = ColorlessMana
}
