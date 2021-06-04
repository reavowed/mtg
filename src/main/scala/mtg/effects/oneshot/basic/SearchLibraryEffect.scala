package mtg.effects.oneshot.basic

import mtg.effects.{OneShotEffect, StackObjectResolutionContext}
import mtg.effects.filters.Filter
import mtg.effects.oneshot.{OneShotEffectChoice, OneShotEffectResult}
import mtg.game.state.{GameActionResult, GameObjectEvent, GameState}
import mtg.game.{ObjectId, PlayerId, Zone}
import mtg.utils.TextUtils._

case class SearchLibraryEffect(objectFilter: Filter[ObjectId]) extends OneShotEffect {
  override def getText(cardName: String): String = "search your library for " + objectFilter.getText(cardName).withArticle
  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): OneShotEffectResult = {
    val player = resolutionContext.controllingPlayer
    val zone = Zone.Library(player)
    val possibleChoices = zone.getState(gameState).view
      .map(_.objectId)
      .filter(objectFilter.isValid(_, resolutionContext, gameState))
      .toSeq
    SearchChoice(player, zone, possibleChoices, resolutionContext)
  }
}

case class ChosenObject(objectId: ObjectId)
case class SearchChoice(
    playerChoosing: PlayerId,
    zone: Zone,
    possibleChoices: Seq[ObjectId],
    resolutionContext: StackObjectResolutionContext)
  extends OneShotEffectChoice
{
  override def handleDecision(serializedDecision: String, currentGameState: GameState): Option[(AnyRef, GameActionResult, StackObjectResolutionContext)] = {
    for {
      id <- serializedDecision.toIntOption
      chosenObjectId <- possibleChoices.find(_.sequentialId == id)
    } yield (ChosenObject(chosenObjectId), Nil, resolutionContext.addIdentifiedObject(chosenObjectId))
  }
  override def temporarilyVisibleZones: Seq[Zone] = Seq(zone)
}
