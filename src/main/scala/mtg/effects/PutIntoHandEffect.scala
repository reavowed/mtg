package mtg.effects

import mtg.events.MoveObjectEvent
import mtg.game.Zone
import mtg.game.state.GameState

case class PutIntoHandEffect(cardIdentifier: CardIdentifier) extends Effect {
  override def text: String = s"put ${cardIdentifier.text} into your hand"
  override def resolve(gameState: GameState, resolutionContext: ResolutionContext): EffectResult = {
    val player = resolutionContext.controller
    MoveObjectEvent(player, cardIdentifier.getCard(gameState, resolutionContext), Zone.Hand(player))
  }
}
