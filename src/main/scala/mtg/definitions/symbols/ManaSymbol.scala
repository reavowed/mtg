package mtg.definitions.symbols

import mtg.definitions.ManaType
import mtg.definitions.colors.Color

sealed abstract class ManaSymbol(textInside: String) extends Symbol(textInside) {
  def colors: Set[Color]
  def manaValue: Int
}

object ManaSymbol {
  sealed abstract class ForType(val manaType: ManaType) extends ManaSymbol(manaType.letter) {
    override def colors: Set[Color] = manaType match {
      case ManaType.Colored(color) => Set(color)
      case ManaType.Colorless => Set.empty
    }
    override def manaValue: Int = 1
  }
  object ForType {
    def unapply(symbol: ManaSymbol.ForType): Some[ManaType] = Some(symbol.manaType)
  }

  object White extends ForType(ManaType.White)
  object Blue extends ForType(ManaType.Blue)
  object Black extends ForType(ManaType.Black)
  object Red extends ForType(ManaType.Red)
  object Green extends ForType(ManaType.Green)
  object Colorless extends ForType(ManaType.Colorless)

  case class Generic(amount: Int) extends ManaSymbol(amount.toString) {
    override def colors: Set[Color] = Set.empty
    override def manaValue: Int = amount
  }

  implicit def intToManaSymbol(amount: Int): ManaSymbol = ManaSymbol.Generic(amount)
}
