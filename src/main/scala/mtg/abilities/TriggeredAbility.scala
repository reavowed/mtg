package mtg.abilities

import mtg.effects.EffectContext
import mtg.effects.condition.Condition
import mtg.game.objects.AbilityOnTheStack
import mtg.game.state.GameState
import mtg.game.{ObjectId, PlayerId}

case class TriggeredAbility(definition: TriggeredAbilityDefinition, sourceId: ObjectId, ownerId: PlayerId) {
  def getCondition(gameState: GameState): Condition = definition.condition.getCondition(gameState, EffectContext(this, gameState))
  def toAbilityOnTheStack: AbilityOnTheStack = AbilityOnTheStack(definition, sourceId, ownerId)
  def getText(gameState: GameState): String = {
    definition.getText(getSourceName(gameState).getOrElse("this object"))
  }
  def getSourceName(gameState: GameState): Option[String] = {
    gameState.gameObjectState.getCurrentOrLastKnownState(sourceId).characteristics.name
  }
}
