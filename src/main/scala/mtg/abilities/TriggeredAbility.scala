package mtg.abilities

import mtg.core.{ObjectId, PlayerId}
import mtg.effects.{EffectContext, InstructionResolutionContext}
import mtg.game.objects.AbilityOnTheStack
import mtg.game.state.history.HistoryEvent
import mtg.game.state.{CurrentCharacteristics, GameAction, GameState}

case class TriggeredAbility(definition: TriggeredAbilityDefinition, sourceId: ObjectId, controllerId: PlayerId) {
  def looksBackInTime: Boolean = definition.triggerCondition.condition.looksBackInTime
  def matchEvent(event: HistoryEvent.ResolvedAction[_], gameState: GameState): Option[Int => PendingTriggeredAbility] = {
    definition.triggerCondition.condition.matchEvent(event, gameState, InstructionResolutionContext.forTriggeredAbility(this))
      .map(c => id => PendingTriggeredAbility(id, this, c))
  }
  def getText(gameState: GameState): String = {
    definition.getText(CurrentCharacteristics.getName(gameState.gameObjectState.getCurrentOrLastKnownState(sourceId)))
  }
}
