package mtg.effects.oneshot.basic

import mtg.effects.{EffectContext, OneShotEffect, StackObjectResolutionContext}
import mtg.effects.oneshot.OneShotEffectResult
import mtg.events.AddManaAction
import mtg.game.state.GameState
import mtg.parts.costs.ManaTypeSymbol

case class AddManaEffect(symbols: ManaTypeSymbol*) extends OneShotEffect {
  override def getText(cardName: String): String = s"Add ${symbols.map(_.text).mkString}."

  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): OneShotEffectResult = {
    (AddManaAction(resolutionContext.controllingPlayer, symbols.map(_.manaType)), resolutionContext)
  }
}
