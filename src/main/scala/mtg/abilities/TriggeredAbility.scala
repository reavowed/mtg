package mtg.abilities

import mtg.effects.EffectContext
import mtg.effects.condition.Condition
import mtg.game.objects.AbilityOnTheStack
import mtg.game.{ObjectId, PlayerId}
import mtg.game.state.GameState

case class TriggeredAbility(definition: TriggeredAbilityDefinition, sourceId: ObjectId, ownerId: PlayerId) {
  def getCondition(gameState: GameState): Condition = definition.condition.getCondition(gameState, EffectContext(this, gameState))
  def toAbilityOnTheStack: AbilityOnTheStack = AbilityOnTheStack(definition, sourceId, ownerId)
}
