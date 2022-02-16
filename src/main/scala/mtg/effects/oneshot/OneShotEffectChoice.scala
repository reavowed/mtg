package mtg.effects.oneshot

import mtg.core.zones.Zone
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.InternalGameAction

abstract class OneShotEffectChoice {
  def playerChoosing: PlayerId
  def parseDecision(serializedDecision: String): Option[(Option[InternalGameAction], StackObjectResolutionContext)]
  def temporarilyVisibleZones: Seq[Zone] = Nil
  def temporarilyVisibleObjects: Seq[ObjectId] = Nil
}
