package mtg.effects

import mtg.game.state.StackObjectWithState
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}

case class ResolutionContext(source: ObjectId, controller: PlayerId, identifiedObjects: Seq[ObjectId], targets: Seq[ObjectOrPlayer]) {
  def addObject(objectId: ObjectId): ResolutionContext = copy(identifiedObjects = identifiedObjects :+ objectId)
  def popTarget: (ObjectOrPlayer, ResolutionContext) = (targets.head, copy(targets = targets.tail))
}
object ResolutionContext {
  def initial(source: StackObjectWithState): ResolutionContext = initial(source.gameObject.objectId, source.controller, source.gameObject.targets)
  def initial(source: ObjectId, controller: PlayerId, targets: Seq[ObjectOrPlayer]): ResolutionContext = ResolutionContext(source, controller, Nil, targets)
}
