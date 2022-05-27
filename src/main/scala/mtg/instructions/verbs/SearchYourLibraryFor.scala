package mtg.instructions.verbs

import mtg.core.zones.Zone
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions._
import mtg.instructions.nouns.ClassNoun
import mtg.utils.TextUtils._

case class SearchYourLibraryFor(noun: ClassNoun[ObjectId]) extends IntransitiveInstructionVerb[PlayerId] {
  override def inflect(verbInflection: VerbInflection, cardName: String): String = {
    Verb.Search.inflect(verbInflection, cardName) + " your library for " + noun.getSingular(cardName).withArticle
  }
  override def resolve(playerId: PlayerId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    val possibleChoices = noun.getAll(gameState, resolutionContext).intersect(gameState.gameObjectState.libraries(playerId).map(_.objectId))
    SearchLibraryChoice(playerId, possibleChoices)
  }
}

// TODO: Handle a player searching another player's library
case class SearchLibraryChoice(
    playerChoosing: PlayerId,
    possibleChoices: Seq[ObjectId])
  extends InstructionChoice
{
  override def parseDecision(
    serializedDecision: String,
    resolutionContext: StackObjectResolutionContext)(
    implicit gameState: GameState
  ): Option[InstructionResult] = {
    for {
      chosenObjectId <- possibleChoices.find(_.toString == serializedDecision)
    } yield resolutionContext.addIdentifiedObject(chosenObjectId)
  }
  override def temporarilyVisibleZones: Seq[Zone] = Seq(Zone.Library(playerChoosing))
}
