package mtg.effects.oneshot.basic

import mtg.effects.oneshot.OneShotEffectResult
import mtg.effects.{OneShotEffect, StackObjectResolutionContext}
import mtg.events.DrawCardsAction
import mtg.game.state.GameState
import mtg.utils.NounPhrase

case class DrawCardsEffect(number: Int) extends OneShotEffect {
  override def getText(cardName: String): String = "draw " + NounPhrase("card").withNumber(number)
  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): OneShotEffectResult = {
    (DrawCardsAction(resolutionContext.controllingPlayer, number), resolutionContext)
  }
}
