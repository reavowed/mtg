package mtg.effects

import mtg.effects.identifiers.Identifier
import mtg.events.GainLifeEvent
import mtg.game.PlayerId
import mtg.game.state.GameState

case class GainLifeEffect(playerIdentifier: Identifier[PlayerId], amount: Int) extends Effect {
  override def getText(cardName: String): String = s"you gain $amount life"
  override def resolve(gameState: GameState, resolutionContext: ResolutionContext): EffectResult = {
    val (player, finalResolutionContext) = playerIdentifier.get(gameState, resolutionContext)
    (GainLifeEvent(player, amount), finalResolutionContext)
  }
}
