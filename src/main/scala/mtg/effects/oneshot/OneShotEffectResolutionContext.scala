package mtg.effects.oneshot

import mtg.game.state.StackObjectWithState
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}

case class OneShotEffectResolutionContext(resolvingObject: ObjectId, controller: PlayerId, identifiedObjects: Seq[ObjectId], targets: Seq[ObjectOrPlayer]) {
  def addObject(objectId: ObjectId): OneShotEffectResolutionContext = copy(identifiedObjects = identifiedObjects :+ objectId)
  def popTarget: (ObjectOrPlayer, OneShotEffectResolutionContext) = (targets.head, copy(targets = targets.tail))
}
object OneShotEffectResolutionContext {
  def initial(resolvingObject: StackObjectWithState): OneShotEffectResolutionContext = initial(resolvingObject.gameObject.objectId, resolvingObject.controller, resolvingObject.gameObject.targets)
  def initial(resolvingObject: ObjectId, controller: PlayerId, targets: Seq[ObjectOrPlayer]): OneShotEffectResolutionContext = OneShotEffectResolutionContext(resolvingObject, controller, Nil, targets)
}
