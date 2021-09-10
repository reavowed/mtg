package mtg.game.stack

import mtg.abilities.{ActivatedAbilityDefinition, ManaAbility}
import mtg.effects.oneshot.{OneShotEffectChoice, OneShotEffectResult}
import mtg.effects.{OneShotEffect, StackObjectResolutionContext}
import mtg.game.state._
import mtg.game.state.history.GameEvent
import mtg.game.{ObjectId, PlayerId, Zone}

case class ResolveManaAbility(player: PlayerId, objectWithAbility: ObjectWithState, ability: ActivatedAbilityDefinition) extends InternalGameAction {
  override def execute(gameState: GameState): InternalGameActionResult = {
    val resolutionContext = StackObjectResolutionContext.forManaAbility(ManaAbility(objectWithAbility.gameObject.objectId, player), gameState)
    ResolveEffects(ability.effectParagraph.effects, resolutionContext)
  }
}
