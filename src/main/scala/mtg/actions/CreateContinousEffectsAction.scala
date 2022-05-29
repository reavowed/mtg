package mtg.actions

import mtg.continuousEffects.ContinuousEffect
import mtg.effects.{EffectContext, InstructionResolutionContext}
import mtg.effects.condition.Condition
import mtg.game.objects.GameObjectState
import mtg.game.state.{DirectGameObjectAction, GameState}

case class CreateContinousEffectsAction(effects: Seq[ContinuousEffect], context: InstructionResolutionContext, endCondition: Condition) extends DirectGameObjectAction[Unit] {
  override def execute(implicit gameState: GameState): DirectGameObjectAction.Result[Unit] = {
    gameState.gameObjectState.addEffects(effects, context, endCondition)
  }
  override def canBeReverted: Boolean = true
}
