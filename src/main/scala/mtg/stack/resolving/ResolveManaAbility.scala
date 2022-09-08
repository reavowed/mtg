package mtg.stack.resolving

import mtg.abilities.{ActivatedAbilityDefinition, ManaAbility}
import mtg.cards.text.SimpleInstructionParagraph
import mtg.definitions.PlayerId
import mtg.effects.InstructionResolutionContext
import mtg.game.state.{DelegatingGameAction, GameAction, GameState, ObjectWithState}

case class ResolveManaAbility(player: PlayerId, objectWithAbility: ObjectWithState, ability: ActivatedAbilityDefinition) extends DelegatingGameAction[Unit] {
  override def delegate(implicit gameState: GameState): GameAction[Unit] = {
    val resolutionContext = InstructionResolutionContext.forManaAbility(ManaAbility(objectWithAbility.gameObject.objectId, player))
    ResolveInstructions(ability.instructions.asInstanceOf[SimpleInstructionParagraph].instructions, resolutionContext)
  }
}
