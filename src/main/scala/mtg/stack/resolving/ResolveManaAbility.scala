package mtg.stack.resolving

import mtg.abilities.{ActivatedAbilityDefinition, ManaAbility}
import mtg.cards.text.SimpleSpellEffectParagraph
import mtg.core.PlayerId
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.{ExecutableGameAction, GameActionResult, GameState, InternalGameAction, ObjectWithState, PartialGameActionResult}

case class ResolveManaAbility(player: PlayerId, objectWithAbility: ObjectWithState, ability: ActivatedAbilityDefinition) extends ExecutableGameAction[Unit] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    val resolutionContext = StackObjectResolutionContext.forManaAbility(ManaAbility(objectWithAbility.gameObject.objectId, player), gameState)
    PartialGameActionResult.child(
      ResolveEffects(ability.effectParagraph.asInstanceOf[SimpleSpellEffectParagraph].effects, resolutionContext))
  }
}
