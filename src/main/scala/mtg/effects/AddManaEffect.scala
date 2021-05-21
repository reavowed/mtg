package mtg.effects

import mtg.events.AddManaEvent
import mtg.game.PlayerIdentifier
import mtg.game.state.GameAction
import mtg.parts.costs.ManaTypeSymbol

case class AddManaEffect(symbols: ManaTypeSymbol*) extends Effect {
  override def text: String = s"Add ${symbols.map(_.text).mkString}."
  override def resolveForAbility(controller: PlayerIdentifier): Seq[GameAction] = Seq(AddManaEvent(controller, symbols.map(_.manaType)))
}

