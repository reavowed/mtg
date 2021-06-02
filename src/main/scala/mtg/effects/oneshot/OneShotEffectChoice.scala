package mtg.effects.oneshot

import mtg.game.state.{GameActionResult, GameObjectEvent, GameState}
import mtg.game.{ObjectId, PlayerId, Zone}

abstract class OneShotEffectChoice {
  def playerChoosing: PlayerId
  def handleDecision(serializedDecision: String, currentGameState: GameState): Option[(AnyRef, GameActionResult, OneShotEffectResolutionContext)]
  def temporarilyVisibleZones: Seq[Zone] = Nil
  def temporarilyVisibleObjects: Seq[ObjectId] = Nil
}
