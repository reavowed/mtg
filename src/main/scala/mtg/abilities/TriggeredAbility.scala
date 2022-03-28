package mtg.abilities

import mtg.core.{ObjectId, PlayerId}
import mtg.effects.EffectContext
import mtg.effects.condition.Condition
import mtg.game.objects.AbilityOnTheStack
import mtg.game.state.{CurrentCharacteristics, GameState, GameUpdate}

case class TriggeredAbility(definition: TriggeredAbilityDefinition, sourceId: ObjectId, ownerId: PlayerId) {
  def conditionMatchesEvent(action: GameUpdate, gameState: GameState): Boolean = {
    definition.triggerCondition.condition.matchesEvent(action, gameState, EffectContext(this))
  }
  def toAbilityOnTheStack: AbilityOnTheStack = AbilityOnTheStack(definition, sourceId, ownerId)
  def getText(gameState: GameState): String = {
    definition.getText(CurrentCharacteristics.getName(gameState.gameObjectState.getCurrentOrLastKnownState(sourceId)))
  }
}
