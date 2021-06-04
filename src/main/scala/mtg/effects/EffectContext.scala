package mtg.effects

import mtg.game.PlayerId

trait EffectContext {
  def controllingPlayer: PlayerId
}
