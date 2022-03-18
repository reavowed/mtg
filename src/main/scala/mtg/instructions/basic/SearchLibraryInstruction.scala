package mtg.instructions.basic

import mtg.core.zones.Zone
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.filters.Filter
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.{GameState, InternalGameAction}
import mtg.instructions.{Instruction, InstructionChoice, InstructionResult}
import mtg.utils.TextUtils._

case class SearchLibraryInstruction(objectFilter: Filter[ObjectId]) extends Instruction {
  override def getText(cardName: String): String = "search your library for " + objectFilter.getNounPhraseTemplate(cardName).singular.withArticle
  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    val player = resolutionContext.controllingPlayer
    val possibleChoices = gameState.gameObjectState.libraries(player).view
      .map(_.objectId)
      .filter((t: ObjectId) => objectFilter.matches(t, gameState, resolutionContext))
      .toSeq
    SearchLibraryChoice(player, possibleChoices, resolutionContext)
  }
}

// TODO: Handle a player searching another player's library
case class SearchLibraryChoice(
    playerChoosing: PlayerId,
    possibleChoices: Seq[ObjectId],
    resolutionContext: StackObjectResolutionContext)
  extends InstructionChoice
{
  override def parseDecision(serializedDecision: String): Option[(Option[InternalGameAction], StackObjectResolutionContext)] = {
    for {
      chosenObjectId <- possibleChoices.find(_.toString == serializedDecision)
    } yield (None, resolutionContext.addIdentifiedObject(chosenObjectId))
  }
  override def temporarilyVisibleZones: Seq[Zone] = Seq(Zone.Library(playerChoosing))
}
