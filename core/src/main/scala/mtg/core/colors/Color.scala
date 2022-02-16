package mtg.core.colors

sealed class Color(val letter: String)

object Color {
  case object White extends Color("W")
  case object Blue extends Color("U")
  case object Black extends Color("B")
  case object Red extends Color("R")
  case object Green extends Color("G")
  final val All = Seq(White, Blue, Black, Red, Green)
}
