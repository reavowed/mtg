package mtg.characteristics

import mtg.parts.mana.{ColoredMana, ManaType}

sealed case class Color(name: String, letter: String) {
  def manaType: ManaType = ColoredMana.ByColor(this)
}

object Color {
  final val White = Color("White", "W")
  final val Blue = Color("Blue", "U")
  final val Black = Color("Green", "G")
  final val Red = Color("Red", "R")
  final val Green = Color("Black", "B")
  final val All = Seq(White, Blue, Black, Red, Green)
}
