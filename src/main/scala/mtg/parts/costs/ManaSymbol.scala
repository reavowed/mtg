package mtg.parts.costs

import mtg.parts.mana.ManaType

sealed class ManaSymbol(symbolInside: String) {
  val text = s"{$symbolInside}"
}

case class ManaTypeSymbol(manaType: ManaType) extends ManaSymbol(manaType.letter)
object ManaTypeSymbol {
  final val All = ManaType.All.map(ManaTypeSymbol(_))
  final val ByManaType: Map[ManaType, ManaTypeSymbol] = All.map(m => m.manaType -> m).toMap
}

