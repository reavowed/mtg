package mtg.definitions

import mtg.definitions.colors.Color

sealed trait ManaType {
  def letter: String
}

object ManaType {
  sealed class Colored(val color: Color) extends ManaType {
    override def letter: String = color.letter
  }
  object Colored {
    def unapply(colored: Colored): Some[Color] = Some(colored.color)
  }

  case object White extends ManaType.Colored(Color.White)
  case object Blue extends ManaType.Colored(Color.Blue)
  case object Black extends ManaType.Colored(Color.Black)
  case object Red extends ManaType.Colored(Color.Red)
  case object Green extends ManaType.Colored(Color.Green)
  case object Colorless extends ManaType {
    override val letter: String = "C"
  }

  final val All = Seq(White, Blue, Black, Red, Green, Colorless)
}


