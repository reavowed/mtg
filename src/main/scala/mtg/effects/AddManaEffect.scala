package mtg.effects

import mtg.parts.costs.ManaTypeSymbol

case class AddManaEffect(symbols: ManaTypeSymbol*) extends Effect {
  override def text: String = s"Add ${symbols.map(_.text).mkString}."
}

