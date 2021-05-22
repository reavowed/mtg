package mtg.parts.mana

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}
import mtg.characteristics.{Color, ColorOrColorless, Colorless}
import mtg.parts.costs.ManaTypeSymbol

@JsonSerialize(using = classOf[ManaType.Serializer])
sealed abstract class ManaType(val letter: String) {
  def symbol: ManaTypeSymbol = ManaTypeSymbol.ByManaType(this)
  def colorOrColorless: ColorOrColorless
}
object ManaType {
  final val All = ColoredMana.All :+ ColorlessMana
  final val ByColorOrColorless: Map[ColorOrColorless, ManaType] = All.map(m => m.colorOrColorless -> m).toMap
  implicit def colorOrColorlessToManaType(colorOrColorless: ColorOrColorless): ManaType = colorOrColorless.manaType

  class Serializer extends JsonSerializer[ManaType] {
    override def serialize(value: ManaType, gen: JsonGenerator, serializers: SerializerProvider): Unit = gen.writeString(value.letter)
  }
}

sealed case class ColoredMana(color: Color) extends ManaType(color.letter) {
  def colorOrColorless: Color = color
}
object ColoredMana {
  final val All = Color.All.map(ColoredMana(_))
  final val ByColor: Map[Color, ColoredMana] = All.map(m => m.color -> m).toMap
}

object ColorlessMana extends ManaType("C") {
  def colorOrColorless: Colorless.type = Colorless
}

