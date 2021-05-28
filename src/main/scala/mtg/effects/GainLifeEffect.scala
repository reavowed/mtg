package mtg.effects

import mtg.events.GainLifeEvent
import mtg.game.state.GameState

case class GainLifeEffect(amount: Int) extends Effect {
  override def text: String = s"you gain $amount life"
  override def resolve(gameState: GameState, resolutionContext: ResolutionContext): EffectResult = {
    GainLifeEvent(resolutionContext.controller, amount)
  }
}
