package mtg.effects
import mtg.game.objects.ObjectId
import mtg.game.state.GameState
import mtg.game.{PlayerIdentifier, Zone}
import mtg.utils.TextUtils._

case class SearchLibraryEffect(objectFilter: ObjectFilter) extends Effect {
  override def text: String = "search your library for " + objectFilter.description.withArticle + " card"
  override def resolve(gameState: GameState, resolutionContext: ResolutionContext): EffectResult = {
    val player = resolutionContext.controller
    val zone = Zone.Library(player)
    val possibleChoices = zone.getState(gameState).view
      .map(gameState.getObjectState)
      .filter(objectFilter.predicate)
      .map(_.gameObject.objectId)
      .toSeq
    SearchChoice(player, zone, possibleChoices, resolutionContext)
  }
}

case class ChosenObject(objectId: ObjectId)
case class SearchChoice(
    playerChoosing: PlayerIdentifier,
    zone: Zone,
    possibleChoices: Seq[ObjectId],
    resolutionContext: ResolutionContext)
  extends EffectChoice
{
  override def handleDecision(serializedDecision: String, currentGameState: GameState): Option[(AnyRef, ResolutionContext)] = {
    for {
      id <- serializedDecision.toIntOption
      chosenObjectId <- possibleChoices.find(_.sequentialId == id)
    } yield (ChosenObject(chosenObjectId), resolutionContext.addObject(chosenObjectId))
  }
}
