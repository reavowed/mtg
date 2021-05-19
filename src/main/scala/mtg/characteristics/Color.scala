package mtg.characteristics

import mtg.parts.mana.{ColoredMana, ColorlessMana, ManaType}

sealed abstract class ColorOrColorless(val letter: String) {
  def manaType: ManaType
}

object ColorOrColorless {
  final val All = Color.All :+ Colorless
}

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
}

case object Colorless extends ColorOrColorless("C") {
  override def manaType: ManaType = ColorlessMana
}
