package mtg.effects

import mtg.events.AddManaEvent
import mtg.game.PlayerId
import mtg.game.state.{GameAction, GameState}
import mtg.parts.costs.ManaTypeSymbol

case class AddManaEffect(symbols: ManaTypeSymbol*) extends Effect {
  override def getText(cardName: String): String = s"Add ${symbols.map(_.text).mkString}."
  override def resolve(gameState: GameState, resolutionContext: ResolutionContext): EffectResult = {
    (AddManaEvent(resolutionContext.controller, symbols.map(_.manaType)), resolutionContext)
  }
}

