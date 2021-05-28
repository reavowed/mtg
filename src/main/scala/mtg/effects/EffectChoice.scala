package mtg.effects

import mtg.game.PlayerIdentifier
import mtg.game.state.GameState

abstract class EffectChoice {
  def playerChoosing: PlayerIdentifier
  def handleDecision(serializedDecision: String, currentGameState: GameState): Option[(AnyRef, ResolutionContext)]
}
