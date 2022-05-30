package mtg.instructions.nounPhrases

import mtg.core.ObjectId
import mtg.effects.InstructionResolutionContext
import mtg.game.state.GameState
import mtg.instructions.grammar.GrammaticalPerson
import mtg.parts.Counter

case class Counters(objectPhrase: SingleIdentifyingNounPhrase[ObjectId]) extends SingleIdentifyingNounPhrase[Map[Counter, Int]] {
  override def getText(cardName: String): String = objectPhrase.getPossessiveText(cardName) + " counters"
  override def person: GrammaticalPerson = GrammaticalPerson.Third
  override def identifySingle(gameState: GameState, resolutionContext: InstructionResolutionContext): (Map[Counter, Int], InstructionResolutionContext) = {
    val (objectId, resultingContext) = objectPhrase.identifySingle(gameState, resolutionContext)
    val counters = gameState.gameObjectState.getCurrentOrLastKnownState(objectId).gameObject.counters
    (counters, resultingContext)
  }
}
