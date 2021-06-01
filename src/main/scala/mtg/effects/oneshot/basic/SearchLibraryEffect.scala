package mtg.effects.oneshot.basic

import mtg.effects.OneShotEffect
import mtg.effects.filters.Filter
import mtg.effects.oneshot.{OneShotEffectChoice, OneShotEffectResolutionContext, OneShotEffectResult}
import mtg.game.state.GameState
import mtg.game.{ObjectId, PlayerId, Zone}
import mtg.utils.TextUtils._

case class SearchLibraryEffect(objectFilter: Filter[ObjectId]) extends OneShotEffect {
  override def getText(cardName: String): String = "search your library for " + objectFilter.text.withArticle
  override def resolve(gameState: GameState, resolutionContext: OneShotEffectResolutionContext): OneShotEffectResult = {
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
    resolutionContext: OneShotEffectResolutionContext)
  extends OneShotEffectChoice
{
  override def handleDecision(serializedDecision: String, currentGameState: GameState): Option[(AnyRef, OneShotEffectResolutionContext)] = {
    for {
      id <- serializedDecision.toIntOption
      chosenObjectId <- possibleChoices.find(_.sequentialId == id)
    } yield (ChosenObject(chosenObjectId), resolutionContext.addIdentifiedObject(chosenObjectId))
  }
}
