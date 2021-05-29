package mtg.effects
import mtg.effects.filters.Filter
import mtg.game.state.GameState
import mtg.game.{ObjectId, PlayerId, Zone}
import mtg.utils.TextUtils._

case class SearchLibraryEffect(objectFilter: Filter[ObjectId]) extends Effect {
  override def getText(cardName: String): String = "search your library for " + objectFilter.text.withArticle + " card"
  override def resolve(gameState: GameState, resolutionContext: ResolutionContext): EffectResult = {
    val player = resolutionContext.controller
    val zone = Zone.Library(player)
    val possibleChoices = zone.getState(gameState).view
      .map(_.objectId)
      .filter(objectFilter.isValid(_, gameState))
      .toSeq
    SearchChoice(player, zone, possibleChoices, resolutionContext)
  }
}

case class ChosenObject(objectId: ObjectId)
case class SearchChoice(
    playerChoosing: PlayerId,
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
