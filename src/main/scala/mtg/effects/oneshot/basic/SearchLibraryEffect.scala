package mtg.effects.oneshot.basic

import mtg.effects.filters.Filter
import mtg.effects.oneshot.{OneShotEffectChoice, OneShotEffectResult}
import mtg.effects.{OneShotEffect, StackObjectResolutionContext}
import mtg.game.state.{GameState, InternalGameAction}
import mtg.game.{ObjectId, PlayerId, Zone}
import mtg.utils.TextUtils._

case class SearchLibraryEffect(objectFilter: Filter[ObjectId]) extends OneShotEffect {
  override def getText(cardName: String): String = "search your library for " + objectFilter.getNounPhraseTemplate(cardName).singular.withArticle
  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): OneShotEffectResult = {
    val player = resolutionContext.controllingPlayer
    val possibleChoices = gameState.gameObjectState.libraries(player).view
      .map(_.objectId)
      .filter(objectFilter.matches(_, resolutionContext, gameState))
      .toSeq
    SearchLibraryChoice(player, possibleChoices, resolutionContext)
  }
}

// TODO: Handle a player searching another player's library
case class SearchLibraryChoice(
    playerChoosing: PlayerId,
    possibleChoices: Seq[ObjectId],
    resolutionContext: StackObjectResolutionContext)
  extends OneShotEffectChoice
{
  override def parseDecision(serializedDecision: String): Option[(Option[InternalGameAction], StackObjectResolutionContext)] = {
    for {
      id <- serializedDecision.toIntOption
      chosenObjectId <- possibleChoices.find(_.sequentialId == id)
    } yield (None, resolutionContext.addIdentifiedObject(chosenObjectId))
  }
  override def temporarilyVisibleZones: Seq[Zone] = Seq(Zone.Library(playerChoosing))
}
