package mtg.parts.costs

import mtg.characteristics.{Color, ColorOrColorless}
import mtg.parts.mana.ManaType

sealed class ManaSymbol(symbolInside: String) {
  val text = s"{$symbolInside}"
}

case class ManaTypeSymbol(manaType: ManaType) extends ManaSymbol(manaType.letter)
object ManaTypeSymbol {
  final val All = ManaType.All.map(ManaTypeSymbol(_))
  final val ByManaType: Map[ManaType, ManaTypeSymbol] = All.map(m => m.manaType -> m).toMap
}

case class GenericManaSymbol(amount: Int) extends ManaSymbol(amount.toString)

object ManaSymbol {
  implicit def intToManaSymbol(amount: Int): ManaSymbol = GenericManaSymbol(amount)
  implicit def colorOrColorlessToManaSymbol(colorOrColorless: ColorOrColorless): ManaSymbol = ManaTypeSymbol(colorOrColorless.manaType)
}
