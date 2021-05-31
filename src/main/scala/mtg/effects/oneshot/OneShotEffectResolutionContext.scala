package mtg.effects.oneshot

import mtg.game.state.StackObjectWithState
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}

case class OneShotEffectResolutionContext(resolvingObject: ObjectId, controller: PlayerId, identifiedObjects: Seq[ObjectOrPlayer], targets: Seq[ObjectOrPlayer]) {
  def addIdentifiedObject(objectId: ObjectOrPlayer): OneShotEffectResolutionContext = copy(identifiedObjects = identifiedObjects :+ objectId)
  def popTarget: (ObjectOrPlayer, OneShotEffectResolutionContext) = {
    val target = targets.head
    (target, addIdentifiedObject(target).copy(targets = targets.tail))
  }
}
object OneShotEffectResolutionContext {
  def initial(resolvingObject: StackObjectWithState): OneShotEffectResolutionContext = initial(resolvingObject.gameObject.objectId, resolvingObject.controller, resolvingObject.gameObject.targets)
  def initial(resolvingObject: ObjectId, controller: PlayerId, targets: Seq[ObjectOrPlayer]): OneShotEffectResolutionContext = OneShotEffectResolutionContext(resolvingObject, controller, Nil, targets)
}
