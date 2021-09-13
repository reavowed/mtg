package mtg.game.stack

import mtg.abilities.{ActivatedAbilityDefinition, ManaAbility}
import mtg.effects.StackObjectResolutionContext
import mtg.game.PlayerId
import mtg.game.state._

case class ResolveManaAbility(player: PlayerId, objectWithAbility: ObjectWithState, ability: ActivatedAbilityDefinition) extends InternalGameAction {
  override def execute(gameState: GameState): InternalGameActionResult = {
    val resolutionContext = StackObjectResolutionContext.forManaAbility(ManaAbility(objectWithAbility.gameObject.objectId, player), gameState)
    ResolveEffects(ability.effectParagraph.effects, resolutionContext)
  }
  override def canBeReverted: Boolean = true
}
