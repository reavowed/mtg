package mtg.effects.oneshot

import mtg.effects.StackObjectResolutionContext
import mtg.game.state.{GameActionResult, GameObjectAction, GameState}
import mtg.game.{ObjectId, PlayerId, Zone}

abstract class OneShotEffectChoice {
  def playerChoosing: PlayerId
  def handleDecision(serializedDecision: String, currentGameState: GameState): Option[(AnyRef, GameActionResult, StackObjectResolutionContext)]
  def temporarilyVisibleZones: Seq[Zone] = Nil
  def temporarilyVisibleObjects: Seq[ObjectId] = Nil
}
