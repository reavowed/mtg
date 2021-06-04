package mtg.effects

import mtg.game.state.StackObjectWithState
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}

case class StackObjectResolutionContext(
    resolvingObject: ObjectId,
    controllingPlayer: PlayerId,
    identifiedObjects: Seq[ObjectOrPlayer],
    targets: Seq[ObjectOrPlayer])
  extends EffectContext
{
  def addIdentifiedObject(objectId: ObjectOrPlayer): StackObjectResolutionContext = copy(identifiedObjects = identifiedObjects :+ objectId)
  def popTarget: (ObjectOrPlayer, StackObjectResolutionContext) = {
    val target = targets.head
    (target, addIdentifiedObject(target).copy(targets = targets.tail))
  }
}
object StackObjectResolutionContext {
  def initial(resolvingObject: StackObjectWithState): StackObjectResolutionContext = initial(resolvingObject.gameObject.objectId, resolvingObject.controller, resolvingObject.gameObject.targets)
  def initial(resolvingObject: ObjectId, controllingPlayer: PlayerId, targets: Seq[ObjectOrPlayer]): StackObjectResolutionContext = StackObjectResolutionContext(resolvingObject, controllingPlayer, Nil, targets)
}
