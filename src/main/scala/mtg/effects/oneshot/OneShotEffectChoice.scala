package mtg.effects.oneshot

import mtg.effects.StackObjectResolutionContext
import mtg.game.state.{InternalGameActionResult, GameObjectEvent, GameState}
import mtg.game.{ObjectId, PlayerId, Zone}

abstract class OneShotEffectChoice {
  def playerChoosing: PlayerId
  def handleDecision(serializedDecision: String, currentGameState: GameState): Option[(AnyRef, InternalGameActionResult, StackObjectResolutionContext)]
  def temporarilyVisibleZones: Seq[Zone] = Nil
  def temporarilyVisibleObjects: Seq[ObjectId] = Nil
}
