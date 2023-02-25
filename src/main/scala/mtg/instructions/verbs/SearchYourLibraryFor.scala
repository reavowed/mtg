package mtg.instructions.verbs

import mtg.definitions.zones.Zone
import mtg.definitions.{ObjectId, PlayerId}
import mtg.effects.InstructionResolutionContext
import mtg.game.state.{Choice, GameState}
import mtg.instructions._
import mtg.instructions.grammar.VerbInflection
import mtg.instructions.nouns.ClassNoun
import mtg.utils.TextUtils._

case class SearchYourLibraryFor(noun: ClassNoun[ObjectId]) extends IntransitiveInstructionVerb[PlayerId] {
  override def inflect(verbInflection: VerbInflection, cardName: String): String = {
    Verb.Search.inflect(verbInflection, cardName) + " your library for " + noun.getSingular(cardName).withArticle
  }
  override def resolve(playerId: PlayerId): InstructionAction = InstructionAction { (resolutionContext, gameState) =>
    val possibleChoices = noun.getAll(gameState, resolutionContext).intersect(gameState.gameObjectState.libraries(playerId).map(_.objectId))
    SearchLibraryChoice(playerId, possibleChoices).map(resolutionContext.addIdentifiedObject)
  }
}

// TODO: Handle a player searching another player's library
case class SearchLibraryChoice(
    playerToAct: PlayerId,
    possibleChoices: Seq[ObjectId])
  extends Choice[ObjectId]
{
  override def handleDecision(
    serializedDecision: String)(
    implicit gameState: GameState
  ): Option[ObjectId] = {
    possibleChoices.find(_.toString == serializedDecision)
  }
  override def temporarilyVisibleZones: Seq[Zone] = Seq(Zone.Library(playerToAct))
}
