package mtg.actions

import mtg.continuousEffects.ContinuousEffect
import mtg.effects.EffectContext
import mtg.effects.condition.Condition
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class CreateContinousEffectsAction(effects: Seq[ContinuousEffect], context: EffectContext, endCondition: Condition) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.addEffects(effects, context, endCondition)
  }
  override def canBeReverted: Boolean = true
}
