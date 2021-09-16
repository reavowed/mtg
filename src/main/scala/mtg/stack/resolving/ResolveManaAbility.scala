package mtg.stack.resolving

import mtg.abilities.{ActivatedAbilityDefinition, ManaAbility}
import mtg.effects.StackObjectResolutionContext
import mtg.game.PlayerId
import mtg.game.state.{GameActionResult, GameState, InternalGameAction, ObjectWithState}

case class ResolveManaAbility(player: PlayerId, objectWithAbility: ObjectWithState, ability: ActivatedAbilityDefinition) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    val resolutionContext = StackObjectResolutionContext.forManaAbility(ManaAbility(objectWithAbility.gameObject.objectId, player), gameState)
    ResolveEffects(ability.effectParagraph.effects, resolutionContext)
  }

  override def canBeReverted: Boolean = true
}
