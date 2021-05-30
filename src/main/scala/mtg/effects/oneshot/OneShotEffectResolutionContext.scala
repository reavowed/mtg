package mtg.effects.oneshot

import mtg.game.state.StackObjectWithState
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}

case class OneShotEffectResolutionContext(source: ObjectId, controller: PlayerId, identifiedObjects: Seq[ObjectId], targets: Seq[ObjectOrPlayer]) {
  def addObject(objectId: ObjectId): OneShotEffectResolutionContext = copy(identifiedObjects = identifiedObjects :+ objectId)
  def popTarget: (ObjectOrPlayer, OneShotEffectResolutionContext) = (targets.head, copy(targets = targets.tail))
}
object OneShotEffectResolutionContext {
  def initial(source: StackObjectWithState): OneShotEffectResolutionContext = initial(source.gameObject.objectId, source.controller, source.gameObject.targets)
  def initial(source: ObjectId, controller: PlayerId, targets: Seq[ObjectOrPlayer]): OneShotEffectResolutionContext = OneShotEffectResolutionContext(source, controller, Nil, targets)
}
