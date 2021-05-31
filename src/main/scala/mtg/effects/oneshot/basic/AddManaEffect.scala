package mtg.effects.oneshot.basic

import mtg.effects.OneShotEffect
import mtg.effects.oneshot.{OneShotEffectResolutionContext, OneShotEffectResult}
import mtg.events.AddManaEvent
import mtg.game.state.GameState
import mtg.parts.costs.ManaTypeSymbol

case class AddManaEffect(symbols: ManaTypeSymbol*) extends OneShotEffect {
  override def getText(cardName: String): String = s"Add ${symbols.map(_.text).mkString}."

  override def resolve(gameState: GameState, resolutionContext: OneShotEffectResolutionContext): OneShotEffectResult = {
    (AddManaEvent(resolutionContext.controller, symbols.map(_.manaType)), resolutionContext)
  }
}
