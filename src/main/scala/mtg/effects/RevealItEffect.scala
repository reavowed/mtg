package mtg.effects

import mtg.game.state.GameState
import mtg.game.state.history.LogEvent

case class RevealEffect(cardIdentifier: CardIdentifier) extends Effect {
  override def text: String = "reveal " + cardIdentifier.text
  override def resolve(gameState: GameState, resolutionContext: ResolutionContext): EffectResult = {
    LogEvent.RevealCard(resolutionContext.controller, cardIdentifier.getCard(gameState, resolutionContext).getName(gameState))
  }
}
