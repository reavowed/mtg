package mtg.instructions.actions

import mtg.actions.CopySpellAction
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.{EffectContext, StackObjectResolutionContext}
import mtg.effects.filters.Filter
import mtg.game.state.{GameState, GameUpdate}
import mtg.instructions.nouns.IndefiniteNounPhrase
import mtg.instructions.{InstructionResult, TransitiveEventMatchingVerb, TransitiveInstructionVerb}
import mtg.text.Verb

case object Copy extends Verb.RegularCaseObject with TransitiveInstructionVerb[PlayerId] with TransitiveEventMatchingVerb {
  override def resolve(playerId: PlayerId, objectId: ObjectId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    (CopySpellAction(playerId, objectId), resolutionContext)
  }
  override def matchesEvent(eventToMatch: GameUpdate, gameState: GameState, effectContext: EffectContext, playerPhrase: IndefiniteNounPhrase[PlayerId], objectPhrase: IndefiniteNounPhrase[ObjectId]): Boolean = eventToMatch match {
    case CopySpellAction(playerId, spellId) if playerPhrase.describes(playerId, gameState, effectContext) && objectPhrase.describes(spellId, gameState, effectContext) =>
      true
    case _ =>
      false
  }
}
