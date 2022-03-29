package mtg.instructions.verbs

import mtg.core.zones.Zone
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.{GameState, InternalGameAction}
import mtg.instructions.nouns.Noun
import mtg.instructions.{InstructionChoice, InstructionResult, IntransitiveInstructionVerb}
import mtg.text.{Verb, VerbInflection}
import mtg.utils.TextUtils._

case class SearchYourLibraryFor(noun: Noun[ObjectId]) extends IntransitiveInstructionVerb[PlayerId] {
  override def inflect(verbInflection: VerbInflection, cardName: String): String = {
    Verb.Search.inflect(verbInflection, cardName) + " your library for " + noun.getSingular(cardName).withArticle
  }
  override def resolve(playerId: PlayerId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    val possibleChoices = noun.getAll(gameState, resolutionContext).intersect(gameState.gameObjectState.libraries(playerId).map(_.objectId))
    SearchLibraryChoice(playerId, possibleChoices, resolutionContext)
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
