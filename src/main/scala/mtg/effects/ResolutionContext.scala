package mtg.effects

import mtg.game.PlayerIdentifier
import mtg.game.objects.ObjectId
import mtg.game.state.ObjectWithState

case class ResolutionContext(source: ObjectId, controller: PlayerIdentifier, identifiedObjects: Seq[ObjectId]) {
  def addObject(objectId: ObjectId): ResolutionContext = copy(identifiedObjects = identifiedObjects :+ objectId)
}
object ResolutionContext {
  def initial(source: ObjectWithState): ResolutionContext = ResolutionContext(source.gameObject.objectId, source.controller.get, Nil)
}
