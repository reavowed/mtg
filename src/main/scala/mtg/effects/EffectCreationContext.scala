package mtg.effects

import mtg.game.PlayerId

case class EffectCreationContext(controllingPlayer: PlayerId) extends EffectContext
