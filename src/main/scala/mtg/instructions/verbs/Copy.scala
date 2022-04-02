package mtg.instructions.verbs

import mtg.actions.CopySpellAction
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.{EffectContext, StackObjectResolutionContext}
import mtg.game.state.{GameAction, GameState}
import mtg.instructions.nounPhrases.IndefiniteNounPhrase
import mtg.instructions.{InstructionResult, TransitiveEventMatchingVerb, TransitiveInstructionVerb}
import mtg.text.Verb

case object Copy extends Verb.RegularCaseObject with TransitiveInstructionVerb[PlayerId, ObjectId] with TransitiveEventMatchingVerb {
  override def resolve(playerId: PlayerId, objectId: ObjectId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    (CopySpellAction(playerId, objectId), resolutionContext)
  }
  override def matchesEvent(eventToMatch: GameAction[_], gameState: GameState, effectContext: EffectContext, playerPhrase: IndefiniteNounPhrase[PlayerId], objectPhrase: IndefiniteNounPhrase[ObjectId]): Boolean = eventToMatch match {
    case CopySpellAction(playerId, spellId) if playerPhrase.describes(playerId, gameState, effectContext) && objectPhrase.describes(spellId, gameState, effectContext) =>
      true
    case _ =>
      false
  }
}
