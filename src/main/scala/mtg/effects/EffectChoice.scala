package mtg.effects

import mtg.game.PlayerId
import mtg.game.state.GameState

abstract class EffectChoice {
  def playerChoosing: PlayerId
  def handleDecision(serializedDecision: String, currentGameState: GameState): Option[(AnyRef, ResolutionContext)]
}
