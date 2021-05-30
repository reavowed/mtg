package mtg.effects.oneshot

import mtg.game.PlayerId
import mtg.game.state.GameState

abstract class OneShotEffectChoice {
  def playerChoosing: PlayerId
  def handleDecision(serializedDecision: String, currentGameState: GameState): Option[(AnyRef, OneShotEffectResolutionContext)]
}
