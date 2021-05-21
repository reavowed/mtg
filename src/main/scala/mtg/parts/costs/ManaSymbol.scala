package mtg.parts.costs

import mtg.characteristics.{Color, ColorOrColorless}
import mtg.parts.mana.{ColoredMana, ColorlessMana, ManaType}

sealed abstract class ManaSymbol(textInside: String) extends Symbol(textInside) {
  def colors: Set[Color]
}

case class ManaTypeSymbol(manaType: ManaType) extends ManaSymbol(manaType.letter) {
  override def colors: Set[Color] = manaType match {
    case ColoredMana(color) => Set(color)
    case ColorlessMana => Set.empty
  }
}
object ManaTypeSymbol {
  final val All = ManaType.All.map(ManaTypeSymbol(_))
  final val ByManaType: Map[ManaType, ManaTypeSymbol] = All.map(m => m.manaType -> m).toMap
}

case class GenericManaSymbol(amount: Int) extends ManaSymbol(amount.toString) {
  override def colors: Set[Color] = Set.empty
}

object ManaSymbol {
  implicit def intToManaSymbol(amount: Int): ManaSymbol = GenericManaSymbol(amount)
  implicit def colorOrColorlessToManaSymbol(colorOrColorless: ColorOrColorless): ManaTypeSymbol = ManaTypeSymbol(colorOrColorless.manaType)
}
