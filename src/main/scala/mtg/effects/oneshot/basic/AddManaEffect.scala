package mtg.effects.oneshot.basic

import mtg.actions.AddManaAction
import mtg.core.symbols.ManaSymbol
import mtg.effects.oneshot.OneShotEffectResult
import mtg.effects.{OneShotEffect, StackObjectResolutionContext}
import mtg.game.state.GameState

case class AddManaEffect(symbols: ManaSymbol*) extends OneShotEffect {
  override def getText(cardName: String): String = s"Add ${symbols.map(_.text).mkString}."

  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): OneShotEffectResult = {
    (AddManaAction(resolutionContext.controllingPlayer, symbols), resolutionContext)
  }
}
