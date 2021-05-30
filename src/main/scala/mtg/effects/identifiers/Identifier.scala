package mtg.effects.identifiers

import mtg.effects.oneshot.OneShotEffectResolutionContext
import mtg.game.ObjectOrPlayer
import mtg.game.state.GameState

trait Identifier[+T <: ObjectOrPlayer] {
  def get(gameState: GameState, resolutionContext: OneShotEffectResolutionContext): (T, OneShotEffectResolutionContext)
  def getText(cardName: String): String
}
