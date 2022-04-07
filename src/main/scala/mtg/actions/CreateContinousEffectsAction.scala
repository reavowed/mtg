package mtg.actions

import mtg.continuousEffects.ContinuousEffect
import mtg.effects.EffectContext
import mtg.effects.condition.Condition
import mtg.game.objects.GameObjectState
import mtg.game.state.{DirectGameObjectAction, GameState}

case class CreateContinousEffectsAction(effects: Seq[ContinuousEffect], context: EffectContext, endCondition: Condition) extends DirectGameObjectAction {
  override def execute(implicit gameState: GameState): GameObjectState = {
    gameState.gameObjectState.addEffects(effects, context, endCondition)
  }
  override def canBeReverted: Boolean = true
}
