package mtg.parts.mana

import mtg.characteristics.Color
import mtg.parts.costs.ManaTypeSymbol

sealed class ManaType(val letter: String) {
  def symbol: ManaTypeSymbol = ManaTypeSymbol.ByManaType(this)
}
object ManaType {
  final val All = ColoredMana.All :+ ColorlessMana
}

sealed case class ColoredMana(color: Color) extends ManaType(color.letter)
object ColoredMana {
  final val All = Color.All.map(ColoredMana(_))
  final val ByColor: Map[Color, ColoredMana] = All.map(m => m.color -> m).toMap
}

object ColorlessMana extends ManaType("C")

